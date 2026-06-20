package com.smartstay.application_mobile_frontend.feature.accommodation.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel
import com.smartstay.application_mobile_frontend.ui.theme.ApplicationmobilefrontendTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelListScreen(
    uiState: HotelListUiState,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onHotelSelected: (Int) -> Unit,
    onNavigateToOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onRefresh()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading) {
                FloatingActionButton(onClick = onNavigateToOptions) {
                    Text(
                        text = "Ver Opciones",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Button(
                        onClick = onRefresh,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Reintentar")
                    }
                }
            }

            uiState.hotels.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "No hay alojamientos disponibles")
                    Button(
                        onClick = onRefresh,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Actualizar")
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding() + 16.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.hotels, key = { it.id }) { hotel ->
                        HotelCard(
                            hotel = hotel,
                            onHotelClick = { onHotelSelected(it.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelListScreenPreview() {
    ApplicationmobilefrontendTheme {
        HotelListScreen(
            uiState = HotelListUiState(
                hotels = listOf(
                    Hotel(
                        id = 1,
                        hostId = 1,
                        name = "Grand Hotel Bolivar",
                        location = "Jr. de la Unión 958, Lima, Peru",
                        imageUrl = "https://placehold.co/600x400/3498DB/FFFFFF?text=Bolivar",
                        description = "Historic hotel in the center of Lima.",
                        basePrice = 85.0,
                        type = "Hotel",
                        amenities = listOf("Wifi", "Restaurante", "Bar")
                    )
                )
            ),
            onRefresh = {},
            onLogout = {},
            onHotelSelected = {},
            onNavigateToOptions = {}
        )
    }
}
