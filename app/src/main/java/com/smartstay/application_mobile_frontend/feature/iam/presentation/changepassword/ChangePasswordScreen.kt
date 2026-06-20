package com.smartstay.application_mobile_frontend.feature.iam.presentation.changepassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import kotlinx.coroutines.delay

/**
 * Pantalla de cambio de contraseña de SmartStay.
 *
 * Permite al usuario autenticado cambiar su contraseña proporcionando
 * la contraseña actual, la nueva y su confirmación, con validación
 * local antes de llamar al ViewModel.
 *
 * @param navController Controlador de navegación.
 * @param viewModel ViewModel de cambio de contraseña inyectado por Hilt.
 */
@Composable
fun ChangePasswordScreen(
    navController: NavHostController,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmNewPasswordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.change_password_success)

    // Mostrar mensaje de éxito y navegar de vuelta
    LaunchedEffect(uiState) {
        if (uiState is ChangePasswordUiState.Success) {
            snackbarHostState.showSnackbar(successMessage)
            viewModel.resetState()
            delay(800)
            navController.popBackStack()
        }
    }

    // Mostrar Snackbar en caso de error
    LaunchedEffect(uiState) {
        if (uiState is ChangePasswordUiState.Error) {
            val errorMessage = (uiState as ChangePasswordUiState.Error).message
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
                text = stringResource(R.string.change_password_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo contraseña actual
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text(stringResource(R.string.change_password_current_label)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.change_password_current_icon_desc)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(
                            imageVector = if (currentPasswordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (currentPasswordVisible) stringResource(R.string.login_hide_password_desc)
                            else stringResource(R.string.login_show_password_desc)
                        )
                    }
                },
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo nueva contraseña
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text(stringResource(R.string.change_password_new_label)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.change_password_new_icon_desc)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(
                            imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (newPasswordVisible) stringResource(R.string.login_hide_password_desc)
                            else stringResource(R.string.login_show_password_desc)
                        )
                    }
                },
                visualTransformation = if (newPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo confirmar nueva contraseña
            OutlinedTextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = { Text(stringResource(R.string.change_password_confirm_new_label)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.change_password_confirm_new_icon_desc)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmNewPasswordVisible = !confirmNewPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmNewPasswordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (confirmNewPasswordVisible) stringResource(R.string.login_hide_password_desc)
                            else stringResource(R.string.login_show_password_desc)
                        )
                    }
                },
                visualTransformation = if (confirmNewPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Cambiar Contraseña
            Button(
                onClick = {
                    when {
                        currentPassword.isBlank() -> {
                            // Validación local: contraseña actual vacía
                        }
                        newPassword.isBlank() -> {
                            // Validación local: nueva contraseña vacía
                        }
                        newPassword != confirmNewPassword -> {
                            // Validación local: contraseñas no coinciden
                        }
                        else -> {
                            viewModel.changePassword(
                                currentPassword = currentPassword,
                                newPassword = newPassword
                            )
                        }
                    }
                },
                enabled = uiState !is ChangePasswordUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState is ChangePasswordUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.change_password_button))
                }
            }
        }
    }
}
