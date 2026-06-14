package com.smartstay.application_mobile_frontend.feature.iam.presentation.signup

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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.smartstay.application_mobile_frontend.R
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.core.navigation.Routes

/**
 * Pantalla de registro de usuario de SmartStay.
 *
 * Permite al usuario crear una cuenta ingresando usuario, contraseña
 * y confirmación de contraseña, con validación local antes de llamar
 * al ViewModel. Navega al dashboard tras un registro exitoso.
 *
 * @param navController Controlador de navegación.
 * @param viewModel ViewModel de registro inyectado por Hilt.
 */
@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Navegación al dashboard en caso de éxito
    LaunchedEffect(uiState) {
        if (uiState is SignUpUiState.Success) {
            navController.navigate(Routes.DASHBOARD) {
                popUpTo(Routes.SIGN_UP) { inclusive = true }
            }
        }
    }

    // Mostrar Snackbar en caso de error
    LaunchedEffect(uiState) {
        if (uiState is SignUpUiState.Error) {
            val errorMessage = (uiState as SignUpUiState.Error).message
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
            // Título
            Text(
                text = stringResource(R.string.signup_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.signup_username_label)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = stringResource(R.string.login_username_icon_desc)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.signup_password_label)) },
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
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(R.string.signup_confirm_password_label)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.icon_confirm_password_desc)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) stringResource(R.string.login_hide_password_desc)
                            else stringResource(R.string.login_show_password_desc)
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrarse
            Button(
                onClick = {
                    when {
                        username.isBlank() -> {
                            // Validación local: usuario vacío
                        }
                        password.isBlank() -> {
                            // Validación local: contraseña vacía
                        }
                        password != confirmPassword -> {
                            // Validación local: contraseñas no coinciden
                        }
                        else -> {
                            viewModel.signUp(
                                username = username,
                                password = password,
                                role = null
                            )
                        }
                    }
                },
                enabled = uiState !is SignUpUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState is SignUpUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.signup_register_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace a inicio de sesión
            TextButton(
                onClick = { navController.navigate(Routes.LOGIN) }
            ) {
                Text(stringResource(R.string.signup_login_link))
            }
        }
    }
}
