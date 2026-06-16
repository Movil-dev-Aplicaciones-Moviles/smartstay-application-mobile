package com.smartstay.application_mobile_frontend.feature.iam.presentation.userlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.smartstay.application_mobile_frontend.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.core.navigation.Routes
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import androidx.compose.material.icons.filled.Badge

/**
 * Pantalla de lista de usuarios de SmartStay.
 *
 * Muestra todos los usuarios del alcance del actor autenticado en una lista vertical
 * y permite navegar al detalle de cada uno o crear un nuevo usuario.
 *
 * @param navController Controlador de navegación.
 * @param viewModel ViewModel de lista de usuarios inyectado por Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    navController: NavHostController,
    viewModel: UserListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissions = viewModel.userPermissions
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar Snackbar en caso de error
    LaunchedEffect(uiState) {
        if (uiState is UserListUiState.Error) {
            val errorMessage = (uiState as UserListUiState.Error).message
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.userlist_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    // NUEVO: Botón para navegar a la lista de perfiles
                    IconButton(onClick = { navController.navigate(Routes.PROFILE_LIST) }) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Ver Perfiles Biográficos"
                        )
                    }
                    // Botón de refresco existente
                    IconButton(onClick = { viewModel.loadUsers() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.userlist_refresh_desc)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (permissions.canCreateUsers) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.CREATE_USER)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.userlist_fab_create_desc)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is UserListUiState.Idle,
                is UserListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UserListUiState.Success -> {
                    val users = (uiState as UserListUiState.Success).users
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                    ) {
                        items(users, key = { it.id }) { user ->
                            UserCard(
                                user = user,
                                onClick = {
                                    navController.navigate(Routes.userDetail(user.id))
                                }
                            )
                        }
                    }
                }

                is UserListUiState.Error -> {
                    val errorMessage = (uiState as UserListUiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.loadUsers() }) {
                            Text(stringResource(R.string.userlist_retry_button))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual de usuario en la lista.
 *
 * Muestra el nombre de usuario, rol, estado y un indicador de navegación.
 *
 * @param user Usuario a mostrar.
 * @param onClick Acción al pulsar la tarjeta.
 */
@Composable
private fun UserCard(
    user: User,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = user.role,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = user.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor(user.status)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.nav_view_user_button),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Retorna un color dinámico según el estado del usuario.
 *
 * @param status Estado del usuario (ej: "Active", "Inactive").
 * @return Color verde si está activo, rojo si está inactivo, gris en otro caso.
 */
@Composable
private fun statusColor(status: String): Color {
    return when (status.lowercase()) {
        "active" -> Color(0xFF4CAF50)
        "inactive" -> Color(0xFFE53935)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
