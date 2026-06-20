package com.smartstay.application_mobile_frontend.feature.accommodation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.model.Hotel
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.HotelListUiState
import com.smartstay.application_mobile_frontend.ui.theme.ApplicationmobilefrontendTheme

@Composable
fun HotelListScreen(
    uiState: HotelListUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToOptions: () -> Unit
) {
    LaunchedEffect(Unit) {
        onRefresh()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        // AGREGADO: El botón flotante para ir a Categorías/Amenities si no está cargando
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Cargando alojamientos...",
                        modifier = Modifier.padding(top = 16.dp)
                    )
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
                        bottom = 80.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.hotels, key = { it.id }) { hotel ->
                        HotelCard(hotel = hotel)
                    }
                }
            }
        }
    }
}

@Composable
private fun HotelCard(hotel: Hotel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = hotel.name, style = MaterialTheme.typography.titleMedium)
            val location = listOfNotNull(hotel.city, hotel.country).joinToString(", ")
            if (location.isNotEmpty()) {
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            hotel.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Text(
                text = "Desde $${"%.2f".format(hotel.lowestPrice)}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
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
                        hostId = 10,
                        name = "Hotel Demo",
                        address = "Calle 1",
                        city = "Madrid",
                        country = "España",
                        imageUrl = "",
                        description = "Un hotel de ejemplo para la vista.",
                        type = "Hotel",
                        amenities = listOf("WiFi", "Desayuno"),
                        lowestPrice = 120.0
                    )
                )
            ),
            onRefresh = {},
            onNavigateToOptions = {}
        )
    }
}