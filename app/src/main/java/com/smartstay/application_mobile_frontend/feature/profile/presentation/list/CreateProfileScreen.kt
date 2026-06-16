package com.smartstay.application_mobile_frontend.feature.profile.presentation.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    navController: NavHostController,
    // Asume que creaste CreateProfileViewModel similar a CreateUserViewModel
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registrar Perfil") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email (Coincidir con IAM)") }, modifier = Modifier.fillMaxWidth())

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Dirección", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Calle") }, modifier = Modifier.weight(2f))
                OutlinedTextField(value = number, onValueChange = { number = it }, label = { Text("Número") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = postalCode, onValueChange = { postalCode = it }, label = { Text("Cód. Postal") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("País") }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Llama a viewModel.createProfile() */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()
            ) {
                Text("Guardar Perfil")
            }
        }
    }
}