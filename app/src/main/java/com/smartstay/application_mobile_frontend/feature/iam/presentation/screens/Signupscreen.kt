package com.smartstay.application_mobile_frontend.feature.iam.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignUpCommand
import com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel.IamViewModel

/**
 * Registration screen composable.
 *
 * Allows new users to create an account by providing a username and password.
 * On success the ViewModel emits [AuthEvent.Navigate] to [AppRoutes.SIGN_IN]
 * (consumed by [SmartStayNavGraph]) — the user must sign in explicitly after
 * registration, as no session is created at this point.
 *
 * Password confirmation is validated client-side before the repository call
 * to avoid an unnecessary network round-trip for a trivially detectable mismatch.
 *
 * @param viewModel          Shared [IamViewModel] injected at the NavGraph level.
 * @param onNavigateToSignIn Triggered when the user taps "Already have an account".
 */
@Suppress("SpellCheckingInspection")
@Composable
fun SignUpScreen(
    viewModel:          IamViewModel,
    onNavigateToSignIn: () -> Unit
) {
    val uiState      by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var username        by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val passwordMismatch = password.isNotBlank()
            && confirmPassword.isNotBlank()
            && password != confirmPassword

    val canSubmit = !uiState.isLoading
            && username.isNotBlank()
            && password.isNotBlank()
            && confirmPassword.isNotBlank()
            && !passwordMismatch

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text  = "Crear cuenta",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text  = "Completa los datos para registrarte",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value         = username,
            onValueChange = {
                username = it
                if (uiState.errors.isNotEmpty()) viewModel.clearErrors()
            },
            label         = { Text("Usuario") },
            singleLine    = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value                = password,
            onValueChange        = {
                password = it
                if (uiState.errors.isNotEmpty()) viewModel.clearErrors()
            },
            label                = { Text("Contraseña") },
            singleLine           = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions      = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions      = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier             = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value                = confirmPassword,
            onValueChange        = { confirmPassword = it },
            label                = { Text("Confirmar contraseña") },
            singleLine           = true,
            isError              = passwordMismatch,
            supportingText       = if (passwordMismatch) {
                { Text("Las contraseñas no coinciden.") }
            } else null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions      = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions      = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (canSubmit) viewModel.signUp(SignUpCommand(username, password))
                }
            ),
            modifier             = Modifier.fillMaxWidth()
        )

        if (uiState.errors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text  = uiState.errors.first(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick  = {
                focusManager.clearFocus()
                viewModel.signUp(SignUpCommand(username, password))
            },
            enabled  = canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color    = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Crear cuenta")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToSignIn) {
            Text("¿Ya tienes cuenta? Iniciar sesión")
        }
    }
}