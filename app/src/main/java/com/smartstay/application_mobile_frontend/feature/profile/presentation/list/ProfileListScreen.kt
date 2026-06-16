package com.smartstay.application_mobile_frontend.feature.profile.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.core.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileListScreen(
    navController: NavHostController,
    viewModel: ProfileListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Perfiles Biográficos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.CREATE_PROFILE) }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Perfil")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is ProfileListUiState.Loading -> CircularProgressIndicator()
                is ProfileListUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is ProfileListUiState.Success -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.profiles) { profile ->
                            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(profile.fullName(), style = MaterialTheme.typography.titleMedium)
                                    Text(profile.email, style = MaterialTheme.typography.bodySmall)
                                    Text(profile.fullAddress(), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}