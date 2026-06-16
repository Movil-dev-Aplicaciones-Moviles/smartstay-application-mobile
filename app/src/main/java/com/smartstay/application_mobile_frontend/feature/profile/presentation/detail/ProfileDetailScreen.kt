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
    // ... (puedes agregar los demás campos de dirección aquí)

    // Lógica de Metavisión: ¿A quién le pertenece este perfil?
    val loadedProfile = (uiState as? ProfileDetailUiState.Success)?.profile
    val isOwnProfile = loadedProfile?.email == currentEmail

    // Regla de Negocio: Si no es mi perfil, los campos quedan bloqueados (Auditoría)
    val isReadOnly = !isOwnProfile

    // Llenamos los datos cuando la API responde
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
                    // Si el empleado no tiene permisos administrativos, esta es su pantalla principal.
                    // No debería poder retroceder a la lista de usuarios.
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
                            readOnly = isReadOnly, // ¡Aquí se aplica el bloqueo dinámico!
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
                            readOnly = true, // El correo JAMÁS se edita porque es la llave técnica
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

                        // El botón de Guardar solo existe si es tu propio perfil
                        if (isOwnProfile) {
                            Button(
                                onClick = { /* viewModel.updateProfile(...) */ },
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