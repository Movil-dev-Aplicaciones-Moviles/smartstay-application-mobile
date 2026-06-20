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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.R
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.UpdateUserRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(
    navController: NavHostController,
    userId: Int,
    @Suppress("DEPRECATION") viewModel: EditUserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.edituser_success)
    val keyboardController = LocalSoftwareKeyboardController.current
    var usernameError by remember { mutableStateOf<String?>(null) }

    // Identificación Segura
    val currentUserId = remember { runBlocking { viewModel.tokenManager.getUserId() } }
    val isMe = userId == currentUserId

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    var username by remember { mutableStateOf("") }
    var hotelIdText by remember { mutableStateOf("") }
    var chainIdText by remember { mutableStateOf("") }

    val loadedUser = (uiState as? EditUserUiState.Loaded)?.user
    LaunchedEffect(loadedUser) {
        loadedUser?.let { user ->
            username = user.username
            hotelIdText = user.hotelId?.toString() ?: ""
            chainIdText = user.chainId?.toString() ?: ""
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is EditUserUiState.Success -> {
                snackbarHostState.showSnackbar(successMessage)
                delay(800.milliseconds) // Resuelto el Warning del Legacy Long
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
                title = { Text(text = stringResource(R.string.edituser_title), style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.edituser_back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (uiState) {
                is EditUserUiState.Idle,
                is EditUserUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is EditUserUiState.Error -> { /* Handled via snackbar */ }
                is EditUserUiState.Saving,
                is EditUserUiState.Loaded,
                is EditUserUiState.Success -> {
                    val isSaving = uiState is EditUserUiState.Saving

                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                usernameError = when {
                                    it.isBlank() -> "El nombre de usuario no puede estar vacío"
                                    it.length < 3 -> "Debe tener al menos 3 caracteres"
                                    else -> null
                                }
                            },
                            label = { Text(stringResource(R.string.edituser_username_label)) },
                            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                            isError = usernameError != null,
                            supportingText = { usernameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Blindaje de IDs: Solo lo muestras si NO eres tú
                        if (!isMe) {
                            OutlinedTextField(
                                value = hotelIdText,
                                onValueChange = { hotelIdText = it },
                                label = { Text(stringResource(R.string.edituser_hotel_id_label)) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Hotel, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSaving
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = chainIdText,
                                onValueChange = { chainIdText = it },
                                label = { Text(stringResource(R.string.edituser_chain_id_label)) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Business, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSaving
                            )
                        } else {
                            // UI Feedback
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                                Text(
                                    text = "Estás editando tu propia cuenta raíz corporativa. Tu nivel organizacional (Chain Admin) se mantiene global e intransferible.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                val request = UpdateUserRequest(
                                    newUsername = username.ifBlank { null },
                                    newPassword = null,
                                    newHotelId = if (isMe) null else hotelIdText.toIntOrNull(),
                                    newChainId = if (isMe) null else chainIdText.toIntOrNull()
                                )
                                viewModel.updateUser(userId, request)
                            },
                            enabled = !isSaving && username.isNotBlank() && usernameError == null,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
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