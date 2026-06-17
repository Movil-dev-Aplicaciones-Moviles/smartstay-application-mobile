package com.smartstay.application_mobile_frontend.feature.options.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartstay.application_mobile_frontend.feature.options.domain.model.Amenity
import com.smartstay.application_mobile_frontend.feature.options.domain.model.HotelCategory

@Composable
fun OptionsScreen(
    viewModel: OptionsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Text(
                text = "Options",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                OptionsContent(
                    amenities = uiState.amenities,
                    categories = uiState.categories
                )
            }
        }
    }
}

@Composable
fun OptionsContent(
    amenities: List<Amenity>,
    categories: List<HotelCategory>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Hotel Categories", style = MaterialTheme.typography.titleLarge)
        }
        items(categories) { category ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(category.name, style = MaterialTheme.typography.titleMedium)
                    Text(category.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        item {
            Text("Amenities", style = MaterialTheme.typography.titleLarge)
        }
        items(amenities) { amenity ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(amenity.name, style = MaterialTheme.typography.titleMedium)
                    Text(amenity.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}