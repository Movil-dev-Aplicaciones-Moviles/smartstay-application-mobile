package com.smartstay.application_mobile_frontend.feature.profile.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.core.navigation.Routes

// IMPORTANTE: Verifica que esta ruta apunte a tu modelo correcto de User del módulo IAM
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileListScreen(
    navController: NavHostController,
    @Suppress("DEPRECATION") viewModel: ProfileListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // CORREGIDO: Uso de mutableIntStateOf por rendimiento en Compose
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Fichas Completadas", "Pendientes de Registro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Directorio Biográfico") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // CORREGIDO: Uso de PrimaryTabRow de Material 3
            PrimaryTabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }

            when (val state = uiState) {
                is ProfileListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProfileListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is ProfileListUiState.Success -> {
                    // TRAMA LÓGICA: Cruzamos los datos de IAM con Perfiles
                    val registradosEmails = state.profiles.map { it.email }.toSet()

                    // Filtramos los que no tienen ficha y evitamos que el chain_admin global salga aquí
                    val pendientes = state.users.filter {
                        it.username !in registradosEmails && it.role.lowercase() != "chain_admin"
                    }

                    if (tabIndex == 0) {
                        // ─── PESTAÑA 0: PERFILES YA CREADOS ───
                        if (state.profiles.isEmpty()) {
                            EmptyStateMessage("No hay fichas registradas aún.")
                        } else {
                            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(state.profiles) { profile ->
                                    ElevatedCard(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("${profile.firstName} ${profile.lastName}", style = MaterialTheme.typography.titleMedium)
                                            Text(profile.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(profile.city, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (pendientes.isEmpty()) {
                            EmptyStateMessage("¡Excelente! Todos los empleados tienen su ficha completa.")
                        } else {
                            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(pendientes) { usuario ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate(Routes.createProfile(usuario.username))
                                            },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(usuario.username, style = MaterialTheme.typography.titleMedium)
                                                Text("Rol Operativo: ${usuario.role}", style = MaterialTheme.typography.bodySmall)
                                            }
                                            Text("Completar Ficha >", color = MaterialTheme.colorScheme.onErrorContainer)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}