package com.smartstay.application_mobile_frontend.feature.iam.presentation.createuser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.R
import com.smartstay.application_mobile_frontend.core.validation.InputValidator
import com.smartstay.application_mobile_frontend.feature.iam.presentation.createuser.CreateUserUiState
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.CreateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.RoleHierarchy
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    navController: NavHostController,
    viewModel: CreateUserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Formulario local
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("staff") } // Rol operacional base por defecto
    var hotelIdText by remember { mutableStateOf("") }
    var chainIdText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Errores de validación local en tiempo de ejecución
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var hotelIdError by remember { mutableStateOf<String?>(null) }
    var chainIdError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateUserUiState.Success -> {
                snackbarHostState.showSnackbar("Usuario interno creado exitosamente")
                delay(800L)
                viewModel.resetState()
                navController.popBackStack()
            }
            is CreateUserUiState.Error -> {
                val errorMsg = (uiState as CreateUserUiState.Error).message
                snackbarHostState.showSnackbar(errorMsg)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Personal", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState is CreateUserUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Nombre de usuario (Email)
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = InputValidator.validateUsername(it)
                        },
                        label = { Text("Nombre de Usuario / Email") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        isError = usernameError != null,
                        supportingText = { usernameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contraseña Inicial
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = InputValidator.validatePassword(it)
                        },
                        label = { Text("Contraseña Inicial") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = { passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ---- NUEVO: Selector desplegable de Roles ----
                    var expandedByRole by remember { mutableStateOf(false) }
                    val rolesDisponibles = remember {
                        RoleHierarchy.getAssignableRoles(actorRole = "chain_admin", targetCurrentRole = "")
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedByRole,
                        onExpandedChange = { expandedByRole = !expandedByRole }
                    ) {
                        OutlinedTextField(
                            value = role,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Rol del Trabajador") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedByRole) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedByRole,
                            onDismissRequest = { expandedByRole = false }
                        ) {
                            rolesDisponibles.forEach { roleOption ->
                                DropdownMenuItem(
                                    text = { Text(roleOption) },
                                    onClick = {
                                        role = roleOption
                                        expandedByRole = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hotel ID (Opcional - Multitenancy)
                    OutlinedTextField(
                        value = hotelIdText,
                        onValueChange = {
                            hotelIdText = it
                            hotelIdError = InputValidator.validateOptionalId(it, "Hotel ID")
                        },
                        label = { Text("Hotel ID (Opcional)") },
                        leadingIcon = { Icon(Icons.Default.Hotel, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = hotelIdError != null,
                        supportingText = { hotelIdError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Chain ID (Opcional - Multitenancy)
                    OutlinedTextField(
                        value = chainIdText,
                        onValueChange = {
                            chainIdText = it
                            chainIdError = InputValidator.validateOptionalId(it, "Chain ID")
                        },
                        label = { Text("Chain ID (Opcional)") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = chainIdError != null,
                        supportingText = { chainIdError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para procesar la creación
                    Button(
                        onClick = {
                            val request = CreateUserRequest(
                                username = username,
                                password = password,
                                role = role.trim().lowercase(),
                                hotelId = hotelIdText.toIntOrNull(),
                                chainId = chainIdText.toIntOrNull()
                            )
                            viewModel.createUser(request)
                        },
                        enabled = username.isNotBlank() && password.isNotBlank() &&
                                usernameError == null && passwordError == null &&
                                hotelIdError == null && chainIdError == null,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Registrar e Implementar")
                    }
                }
            }
        }
    }
}