package com.smartstay.application_mobile_frontend.feature.iam.presentation.userdetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartstay.application_mobile_frontend.R
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.core.navigation.Routes
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserPermissions
import com.smartstay.application_mobile_frontend.feature.iam.presentation.assignrole.AssignRoleDialog
import androidx.compose.material.icons.filled.Check

/**
 * Pantalla de detalle de usuario de SmartStay.
 *
 * Muestra todos los campos del modelo [User] y ofrece botones contextuales
 * para editar, asignar rol y desactivar al usuario.
 *
 * @param navController Controlador de navegación.
 * @param userId ID del usuario a visualizar.
 * @param viewModel ViewModel de detalle de usuario inyectado por Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    navController: NavHostController,
    userId: Int,
    actorRole: String,
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissions = viewModel.userPermissions
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar usuario al entrar o cambiar de userId
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    // Mostrar Snackbar en caso de error
    LaunchedEffect(uiState) {
        if (uiState is UserDetailUiState.Error) {
            val errorMessage = (uiState as UserDetailUiState.Error).message
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    // Diálogo de confirmación para desactivar usuario
    var showDeactivateDialog by remember { mutableStateOf(false) }

    // Diálogo de asignación de rol
    var showAssignRoleDialog by remember { mutableStateOf(false) }

    if (showDeactivateDialog) {
        val user = (uiState as? UserDetailUiState.Success)?.user
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text(stringResource(R.string.deactivate_dialog_title)) },
            text = {
                Text(
                    stringResource(R.string.deactivate_dialog_message, user?.username ?: "")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deactivateUser(userId)
                        showDeactivateDialog = false
                    }
                ) {
                    Text(stringResource(R.string.deactivate_dialog_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) {
                    Text(stringResource(R.string.deactivate_dialog_cancel))
                }
            }
        )
    }

    if (showAssignRoleDialog) {
        val user = (uiState as? UserDetailUiState.Success)?.user
        AssignRoleDialog(
            actorRole = actorRole,
            targetUserId = userId,
            targetCurrentRole = user?.role ?: "",
            onDismiss = { showAssignRoleDialog = false },
            onAssignRole = { role ->
                viewModel.assignRole(userId, role)
                showAssignRoleDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.userdetail_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.userdetail_back_desc)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is UserDetailUiState.Idle,
                is UserDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UserDetailUiState.Error -> {
                    val errorMessage = (uiState as UserDetailUiState.Error).message
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
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.loadUser(userId) }) {
                            Text(stringResource(R.string.userdetail_retry_button))
                        }
                    }
                }

                is UserDetailUiState.Success -> {
                    val user = (uiState as UserDetailUiState.Success).user
                    UserDetailContent(
                        user = user,
                        permissions = permissions,
                        onEdit = { navController.navigate(Routes.editUser(user.id)) },
                        onAssignRole = { showAssignRoleDialog = true },
                        onDeactivate = { showDeactivateDialog = true },
                        onActivate = { viewModel.activateUser(user.id) }
                    )
                }
            }
        }
    }
}

/**
 * Contenido de la pantalla de detalle de usuario.
 *
 * Muestra una tarjeta con los datos del usuario y botones de acción
 * condicionados por los permisos del actor autenticado.
 *
 * @param user Usuario a mostrar.
 * @param permissions Permisos del actor autenticado.
 * @param onEdit Acción al pulsar "Editar Usuario".
 * @param onAssignRole Acción al pulsar "Asignar Rol".
 * @param onDeactivate Acción al pulsar "Desactivar Usuario".
 */
@Composable
private fun UserDetailContent(
    user: User,
    permissions: UserPermissions,
    onEdit: () -> Unit,
    onAssignRole: () -> Unit,
    onDeactivate: () -> Unit,
    onActivate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ---- Sección de datos del usuario ----
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circular con inicial del username
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.username.first().uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Username
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rol
                Text(
                    text = user.role,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Estado con color dinámico
                Text(
                    text = user.status,
                    style = MaterialTheme.typography.bodyLarge,
                    color = statusColor(user.status),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(8.dp))

                // Campos adicionales
                LabelValueRow(label = stringResource(R.string.userdetail_field_id), value = user.id.toString())
                LabelValueRow(
                    label = stringResource(R.string.userdetail_field_hotel_id),
                    value = user.hotelId?.toString() ?: stringResource(R.string.userdetail_no_hotel)
                )
                LabelValueRow(
                    label = stringResource(R.string.userdetail_field_chain_id),
                    value = user.chainId?.toString() ?: stringResource(R.string.userdetail_no_chain)
                )
                LabelValueRow(
                    label = stringResource(R.string.userdetail_field_created),
                    value = user.createdAt ?: stringResource(R.string.userdetail_unknown_date)
                )
                LabelValueRow(
                    label = stringResource(R.string.userdetail_field_updated),
                    value = user.updatedAt ?: stringResource(R.string.userdetail_unknown_date)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---- Sección de acciones ----
        if (permissions.canEditUser(targetRole = user.role)) {
            Button(
                onClick = onEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.userdetail_edit_button))
            }
        }

        if (permissions.canAssignRole(targetCurrentRole = user.role)) {
            Button(
                onClick = onAssignRole,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ManageAccounts,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.userdetail_assign_role_button))
            }
        }

        if (permissions.canDeactivateUser(targetRole = user.role)) {
            if (user.status.lowercase() == "inactive") {
                Button(
                    onClick = onActivate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50), // Verde esmeralda operacional
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Activar Usuario")
                }
            } else {
                // El usuario está activo: mostramos el botón clásico de DESACTIVAR (Rojo)
                Button(
                    onClick = onDeactivate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.userdetail_deactivate_button))
                }
            }
        }
    }
}

/**
 * Fila de etiqueta y valor para mostrar campos de información.
 *
 * @param label Nombre del campo.
 * @param value Valor del campo.
 */
@Composable
private fun LabelValueRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
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