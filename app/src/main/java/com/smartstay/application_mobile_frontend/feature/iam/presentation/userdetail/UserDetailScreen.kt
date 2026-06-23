package com.smartstay.application_mobile_frontend.feature.iam.presentation.userdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.R
import com.smartstay.application_mobile_frontend.core.navigation.Routes
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserPermissions
import com.smartstay.application_mobile_frontend.feature.iam.presentation.assignrole.AssignRoleDialog
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    navController: NavHostController,
    userId: Int,
    actorRole: String,
    @Suppress("DEPRECATION") viewModel: UserDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissions = viewModel.userPermissions
    val snackbarHostState = remember { SnackbarHostState() }

    // Identificación segura
    val currentUserId = remember { runBlocking { viewModel.tokenManager.getUserId() } }
    val isMe = userId == currentUserId

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    LaunchedEffect(uiState) {
        if (uiState is UserDetailUiState.Error) {
            val errorMessage = (uiState as UserDetailUiState.Error).message
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showAssignRoleDialog by remember { mutableStateOf(false) }

    if (showDeactivateDialog) {
        val user = (uiState as? UserDetailUiState.Success)?.user
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text(stringResource(R.string.deactivate_dialog_title)) },
            text = { Text(stringResource(R.string.deactivate_dialog_message, user?.username ?: "")) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deactivateUser(userId)
                    showDeactivateDialog = false
                }) {
                    Text(stringResource(R.string.deactivate_dialog_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) { Text(stringResource(R.string.deactivate_dialog_cancel)) }
            }
        )
    }

    if (showAssignRoleDialog) {
        val user = (uiState as? UserDetailUiState.Success)?.user
        AssignRoleDialog(
            actorRole = actorRole,
            targetUserId = userId,
            targetCurrentRole = user?.role ?: "",
            onDismiss = { showAssignRoleDialog = false },
            onAssignRole = { role ->
                viewModel.assignRole(userId, role)
                showAssignRoleDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.userdetail_title), style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Routes.MAIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.userdetail_back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (uiState) {
                is UserDetailUiState.Idle,
                is UserDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UserDetailUiState.Error -> {
                    val errorMessage = (uiState as UserDetailUiState.Error).message
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = errorMessage, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.loadUser(userId) }) { Text(stringResource(R.string.userdetail_retry_button)) }
                    }
                }
                is UserDetailUiState.Success -> {
                    val user = (uiState as UserDetailUiState.Success).user
                    UserDetailContent(
                        user = user,
                        permissions = permissions,
                        isMe = isMe,
                        onEdit = { navController.navigate(Routes.editUser(user.id)) },
                        onAssignRole = { showAssignRoleDialog = true },
                        onDeactivate = { showDeactivateDialog = true },
                        onActivate = { viewModel.activateUser(user.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserDetailContent(
    user: User,
    permissions: UserPermissions,
    isMe: Boolean,
    onEdit: () -> Unit,
    onAssignRole: () -> Unit,
    onDeactivate: () -> Unit,
    onActivate: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.username.first().uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = user.username, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.role, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.status, style = MaterialTheme.typography.bodyLarge, color = statusColor(user.status), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                LabelValueRow(label = stringResource(R.string.userdetail_field_id), value = user.id.toString())

                // Mapeo Inteligente y Seguro de Sucursales
                LabelValueRow(
                    label = "Sucursal Asignada",
                    value = if (user.hotelId == 1) "Hotel Costa Azul (Sede Principal)" else user.hotelId?.toString() ?: "No asignado"
                )
                LabelValueRow(
                    label = "Sede Organizacional",
                    value = if (user.role.lowercase() == "chain_admin") "Sede Corporativa Global" else user.chainId?.toString() ?: "Local"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (permissions.canEditUser(targetRole = user.role)) {
            Button(onClick = onEdit, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.userdetail_edit_button))
            }
        }

        // Bloqueo estricto: no puedes cambiarte el rol a ti mismo
        if (permissions.canAssignRole(targetCurrentRole = user.role) && !isMe) {
            Button(onClick = onAssignRole, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Icon(imageVector = Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.userdetail_assign_role_button))
            }
        }

        // Bloqueo estricto: no puedes desactivarte a ti mismo
        if (permissions.canDeactivateUser(targetRole = user.role) && !isMe) {
            if (user.status.lowercase() == "inactive") {
                Button(
                    onClick = onActivate,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Activar Usuario")
                }
            } else {
                Button(
                    onClick = onDeactivate,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError)
                ) {
                    Icon(imageVector = Icons.Default.Block, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.userdetail_deactivate_button))
                }
            }
        }
    }
}

@Composable
private fun LabelValueRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun statusColor(status: String): Color {
    return when (status.lowercase()) {
        "active" -> Color(0xFF4CAF50)
        "inactive" -> Color(0xFFE53935)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}