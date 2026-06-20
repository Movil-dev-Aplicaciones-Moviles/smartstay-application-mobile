package com.smartstay.application_mobile_frontend.feature.profile.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ArrowBack // ─── Ícono actualizado importado aquí
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.core.navigation.Routes
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    navController: NavHostController,
    profileId: Int,
    @Suppress("DEPRECATION") viewModel: ProfileDetailViewModel = hiltViewModel() // ─── Warning de Hilt suprimido
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissions = viewModel.permissions
    val currentEmail = viewModel.currentEmail
    val context = LocalContext.current

    // Disparamos la carga al entrar a la cancha
    LaunchedEffect(profileId) {
        viewModel.loadProfile(profileId)
    }

    // Estados del formulario local
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // ─── OPTIMIZACIÓN REACTIVA DE PERMISOS ───
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
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isReadOnly) "Ficha del Empleado" else "Mi Perfil", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    if (permissions.canManageUsers) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            // ─── Ícono corregido a la versión AutoMirrored
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                },
                actions = {
                    if (!permissions.canManageUsers || isOwnProfile) {
                        IconButton(onClick = {
                            viewModel.logout(onSuccess = {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            })
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Banner Informativo: Identificador de Cuenta Ligado de forma natural y visual
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Cuenta de Acceso",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = email,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Notificación visual de modo auditoría
                        if (isReadOnly) {
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                                Text(
                                    text = "Modo Auditoría: Estás viendo la información biográfica de otro miembro del equipo.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        Text(
                            text = "Información de Identidad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Formulario de Datos Simplificado al Máximo
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("Nombre") },
                            readOnly = isReadOnly,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Apellidos") },
                            readOnly = isReadOnly,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // El botón de Guardar solo aparece si el perfil es tuyo
                        if (isOwnProfile) {
                            Button(
                                onClick = {
                                    // Placeholder para actualización: Simulación de éxito y navegación
                                    Toast.makeText(context, "Información actualizada correctamente", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Routes.DASHBOARD) {
                                        popUpTo(Routes.PROFILE_DETAIL) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text("Guardar Cambios Personales", fontWeight = FontWeight.Bold)
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