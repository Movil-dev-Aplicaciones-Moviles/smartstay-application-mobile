package com.smartstay.application_mobile_frontend.feature.profile.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    navController: NavHostController,
    profileId: Int,
    viewModel: ProfileDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissions = viewModel.permissions
    val currentEmail = viewModel.currentEmail

    // Disparamos la carga al entrar a la cancha
    LaunchedEffect(profileId) {
        viewModel.loadProfile(profileId)
    }

    // Estados del formulario local
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    // ─── OPTIMIZACIÓN REACTIVA DE PERMISOS ───
    // Extraemos el perfil de forma segura garantizando que el cálculo sea dinámico en cada ciclo
    val loadedProfile = (uiState as? ProfileDetailUiState.Success)?.profile

    // Condición reactiva: Evaluamos si el perfil cargado coincide con nuestra sesión
    val isOwnProfile = remember(loadedProfile) { loadedProfile?.email == currentEmail }
    val isReadOnly = remember(loadedProfile) { !isOwnProfile }

    // Sincronizamos las cajas de texto cuando la API responda exitosamente
    LaunchedEffect(loadedProfile) {
        loadedProfile?.let {
            firstName = it.firstName
            lastName = it.lastName
            email = it.email
            city = it.city
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isOwnProfile) "Mi Perfil" else "Ficha del Empleado") },
                navigationIcon = {
                    // Si el administrador está auditando, puede volver a la lista de usuarios.
                    if (permissions.canManageUsers) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (uiState) {
                is ProfileDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProfileDetailUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Notificación visual de modo auditoría
                        if (isReadOnly) {
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                                Text(
                                    text = "Modo Auditoría: Estás viendo la información de otro empleado.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // Formulario de Datos
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("Nombre") },
                            readOnly = isReadOnly, // Bloqueo dinámico recalculado por el remember
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Apellidos") },
                            readOnly = isReadOnly,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo Electrónico") },
                            readOnly = true, // El correo JAMÁS se edita porque es la llave de negocio
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Ciudad de residencia") },
                            readOnly = isReadOnly,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // El botón de Guardar solo aparece si el perfil es tuyo
                        if (isOwnProfile) {
                            Button(
                                onClick = {
                                    // Aquí llamarás al método PUT para actualizar tus datos personales
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Text("Guardar Cambios Personales")
                            }
                        }
                    }
                }
                is ProfileDetailUiState.Error -> {
                    Text(
                        text = (uiState as ProfileDetailUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {}
            }
        }
    }
}