package com.smartstay.application_mobile_frontend.feature.accommodation.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHotelScreen(
    navController: NavHostController,
    hotelId: Int,
    viewModel: EditHotelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    val selectedAmenities = remember { mutableStateListOf<String>() }

    val availableAmenities = listOf("Wifi", "Restaurante", "Bar", "Piscina", "Gimnasio", "Spa")

    LaunchedEffect(hotelId) {
        viewModel.loadHotel(hotelId)
    }

    LaunchedEffect(uiState.hotel) {
        uiState.hotel?.let { hotel ->
            name = hotel.name
            address = hotel.address
            city = hotel.city
            country = hotel.country
            imageUrl = hotel.imageUrl
            description = hotel.description ?: ""
            type = hotel.type
            selectedAmenities.clear()
            selectedAmenities.addAll(hotel.amenities)
        }
    }

    LaunchedEffect(uiState.isUpdateSuccess) {
        if (uiState.isUpdateSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Propiedad", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.hotel == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Hotel") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Text("Ubicación", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ciudad") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("País") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Amenidades", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableAmenities.forEach { amenity ->
                        FilterChip(
                            selected = selectedAmenities.contains(amenity),
                            onClick = {
                                if (selectedAmenities.contains(amenity)) selectedAmenities.remove(amenity)
                                else selectedAmenities.add(amenity)
                            },
                            label = { Text(amenity) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        uiState.hotel?.let { currentHotel ->
                            viewModel.updateHotel(
                                hotelId = hotelId,
                                hostId = currentHotel.hostId,
                                name = name,
                                address = address,
                                city = city,
                                country = country,
                                imageUrl = imageUrl,
                                description = description,
                                type = type,
                                amenities = selectedAmenities.toList()
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = name.isNotBlank() && !uiState.isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Actualizar Propiedad", fontWeight = FontWeight.Bold)
                    }
                }

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
