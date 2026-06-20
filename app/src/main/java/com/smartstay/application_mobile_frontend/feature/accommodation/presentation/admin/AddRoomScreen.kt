package com.smartstay.application_mobile_frontend.feature.accommodation.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomScreen(
    navController: NavHostController,
    hotelId: Int,
    viewModel: AddRoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var selectedRoomType by remember { mutableStateOf<Int?>(null) }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    val availableAmenities = listOf("Wifi", "TV", "Minibar", "Aire Acondicionado", "Caja Fuerte")
    val selectedAmenities = remember { mutableStateListOf<String>() }
    
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Habitación", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Tipo de Habitación", style = MaterialTheme.typography.titleMedium)
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = uiState.roomTypes.find { it.id == selectedRoomType }?.name ?: "Seleccione tipo",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.roomTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                selectedRoomType = type.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio por Noche") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("S/ ") }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción / Número") },
                placeholder = { Text("Ej: Habitación 101 - Vista al mar") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text("Amenidades de Habitación", style = MaterialTheme.typography.titleMedium)
            
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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    selectedRoomType?.let { typeId ->
                        viewModel.createRoom(
                            hotelId = hotelId,
                            roomTypeId = typeId,
                            price = price.toDoubleOrNull() ?: 0.0,
                            description = description,
                            amenities = selectedAmenities.toList()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = selectedRoomType != null && price.isNotBlank() && !uiState.isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Habitación", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
