package com.smartstay.application_mobile_frontend.feature.iam.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.smartstay.application_mobile_frontend.R
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.core.navigation.Routes
import com.smartstay.application_mobile_frontend.core.validation.InputValidator
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserPermissions
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de sesión de SmartStay.
 * Uso exclusivo para personal operativo y administrativo del hotel/cadena.
 *
 * Permite al usuario ingresar sus credenciales, validar los campos
 * localmente y navegar al listado de usuarios tras un login exitoso.
 *
 * @param navController Controlador de navegación.
 * @param viewModel ViewModel de login inyectado por Hilt.
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val userRole = (uiState as LoginUiState.Success).authenticatedUser.user.role
            val permissions = UserPermissions(userRole)

            val destination = if (permissions.canManageUsers) {
                Routes.USER_LIST
            } else {
                // Al loguearse, el backend ya retorna su id de usuario/perfil
                Routes.profileDetail((uiState as LoginUiState.Success).authenticatedUser.user.id)
            }

            navController.navigate(destination) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    // Mostrar Snackbar en caso de error retornado por la API
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Error) {
            val errorMessage = (uiState as LoginUiState.Error).message
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la Aplicación Administrativa
            Text(
                text = stringResource(R.string.login_smartstay_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de usuario (Email o identificador alfanumérico)
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.login_username_label)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = stringResource(R.string.login_username_icon_desc)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is LoginUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña con toggle de visibilidad
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.login_password_label)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.login_password_icon_desc)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) stringResource(R.string.login_hide_password_desc)
                            else stringResource(R.string.login_show_password_desc)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is LoginUiState.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Ingresar con validaciones locales robustas
            Button(
                onClick = {
                    // 1. Validaciones de presencia e integridad con el InputValidator del core
                    val usernameError = InputValidator.validateUsername(username)
                    val passwordError = InputValidator.validatePassword(password)

                    when {
                        usernameError != null -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(usernameError)
                            }
                        }
                        passwordError != null -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(passwordError)
                            }
                        }
                        else -> {
                            // Si pasa las reglas locales, dispara la petición de red
                            viewModel.signIn(username, password)
                        }
                    }
                },
                enabled = uiState !is LoginUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.login_sign_in_button))
                }
            }
        }
    }
}