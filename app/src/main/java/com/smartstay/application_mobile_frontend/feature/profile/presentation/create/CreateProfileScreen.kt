package com.smartstay.application_mobile_frontend.feature.profile.presentation.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.feature.profile.data.dto.CreateProfileRequest
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    navController: NavHostController,
    prefilledEmail: String,
    @Suppress("DEPRECATION") viewModel: CreateProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cálculo inicial de sugerencia
    val sugerenciaNombre = remember(prefilledEmail) {
        if (prefilledEmail.contains("@")) {
            prefilledEmail.substringBefore('@').replaceFirstChar { it.uppercase() }
        } else {
            prefilledEmail.replaceFirstChar { it.uppercase() }
        }
    }

    var firstName by remember { mutableStateOf(sugerenciaNombre) }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(prefilledEmail) }

    // Sincronización reactiva del correo inyectado
    LaunchedEffect(prefilledEmail) {
        if (prefilledEmail.isNotBlank()) {
            email = prefilledEmail
            firstName = sugerenciaNombre
        }
    }

    // Efecto reactivo para volver al menú cuando el perfil se crea con éxito
    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateProfileUiState.Success -> {
                snackbarHostState.showSnackbar("¡Ficha biográfica creada con éxito!")
                delay(800.milliseconds)
                navController.popBackStack()
            }
            is CreateProfileUiState.Error -> {
                val errorMsg = (uiState as CreateProfileUiState.Error).message
                snackbarHostState.showSnackbar(errorMsg)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Perfil", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // CABECERA AMIGABLE: Presentación limpia del ID de cuenta activa sin usar text fields
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Configurando Ficha de Empleado",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Esta información biográfica se asociará permanentemente a la cuenta activa corporativa: $email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Text(
                text = "Datos Personales",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellidos") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val request = CreateProfileRequest(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        street = "-",
                        number = "-",
                        city = "-",
                        postalCode = "-",
                        country = "-"
                    )
                    viewModel.createProfile(request)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && uiState !is CreateProfileUiState.Loading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState is CreateProfileUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Ficha Biográfica", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}