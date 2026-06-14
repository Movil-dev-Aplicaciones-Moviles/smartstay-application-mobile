package com.smartstay.application_mobile_frontend.feature.iam.presentation.edituser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.smartstay.application_mobile_frontend.R
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.core.navigation.Routes
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.UpdateUserRequest
import kotlinx.coroutines.delay

/**
 * Pantalla de edición de usuario de SmartStay.
 *
 * Permite modificar los campos [username], [role], [status], [hotelId] y [chainId]
 * de un usuario existente mediante una actualización parcial (PUT).
 *
 * @param navController Controlador de navegación.
 * @param userId ID del usuario a editar.
 * @param viewModel ViewModel de edición de usuario inyectado por Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(
    navController: NavHostController,
    userId: Int,
    viewModel: EditUserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.edituser_success)

    // Cargar usuario al entrar
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    // Estados locales del formulario
    var username by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var hotelIdText by remember { mutableStateOf("") }
    var chainIdText by remember { mutableStateOf("") }

    // Inicializar campos cuando el usuario se carga
    val loadedUser = (uiState as? EditUserUiState.Loaded)?.user
    LaunchedEffect(loadedUser) {
        loadedUser?.let { user ->
            username = user.username
            role = user.role
            status = user.status
            hotelIdText = user.hotelId?.toString() ?: ""
            chainIdText = user.chainId?.toString() ?: ""
        }
    }

    // Efectos de Snackbar y navegación
    LaunchedEffect(uiState) {
        when (uiState) {
            is EditUserUiState.Success -> {
                snackbarHostState.showSnackbar(successMessage)
                delay(800L)
                navController.popBackStack()
            }
            is EditUserUiState.Error -> {
                val errorMessage = (uiState as EditUserUiState.Error).message
                snackbarHostState.showSnackbar(errorMessage)
            }
            else -> { /* no-op */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edituser_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.edituser_back_desc)
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
                is EditUserUiState.Idle,
                is EditUserUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is EditUserUiState.Error -> {
                    // El error se muestra via Snackbar; se deja el contenido previo si había algo
                }

                is EditUserUiState.Saving,
                is EditUserUiState.Loaded,
                is EditUserUiState.Success -> {
                    val isSaving = uiState is EditUserUiState.Saving

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Username
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text(stringResource(R.string.edituser_username_label)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Role
                        OutlinedTextField(
                            value = role,
                            onValueChange = { role = it },
                            label = { Text(stringResource(R.string.edituser_role_label)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        )
                        Text(
                            text = stringResource(R.string.edituser_role_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Status
                        OutlinedTextField(
                            value = status,
                            onValueChange = { status = it },
                            label = { Text(stringResource(R.string.edituser_status_label)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Circle,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        )
                        Text(
                            text = stringResource(R.string.edituser_status_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Hotel ID
                        OutlinedTextField(
                            value = hotelIdText,
                            onValueChange = { hotelIdText = it },
                            label = { Text(stringResource(R.string.edituser_hotel_id_label)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Hotel,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Chain ID
                        OutlinedTextField(
                            value = chainIdText,
                            onValueChange = { chainIdText = it },
                            label = { Text(stringResource(R.string.edituser_chain_id_label)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón guardar
                        Button(
                            onClick = {
                                val request = UpdateUserRequest(
                                    username = username.ifBlank { null },
                                    role = role.ifBlank { null },
                                    status = status.ifBlank { null },
                                    hotelId = hotelIdText.toIntOrNull(),
                                    chainId = chainIdText.toIntOrNull()
                                )
                                viewModel.updateUser(userId, request)
                            },
                            enabled = !isSaving && username.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(stringResource(R.string.edituser_save_button))
                            }
                        }
                    }
                }
            }
        }
    }
}
