package com.smartstay.application_mobile_frontend.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.HotelListViewModel
import com.smartstay.application_mobile_frontend.feature.iam.presentation.screens.SignInScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel.IamViewModel
import com.smartstay.application_mobile_frontend.feature.options.presentation.HotelListScreen
import com.smartstay.application_mobile_frontend.feature.options.presentation.OptionsScreen
import com.smartstay.application_mobile_frontend.feature.options.presentation.OptionsViewModel

@Composable
fun SmartStayNavGraph() {
    val navController = rememberNavController()

    val iamViewModel: IamViewModel = hiltViewModel()
    val uiState by iamViewModel.uiState.collectAsState()

    val startDestination = if (uiState.isSignedIn) "dashboard" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            SignInScreen(
                viewModel = iamViewModel,
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            if (!uiState.isSignedIn) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            } else {
                val hotelListViewModel: HotelListViewModel = hiltViewModel()
                val hotelUiState by hotelListViewModel.uiState.collectAsState()

                HotelListScreen(
                    uiState = hotelUiState,
                    onRefresh = hotelListViewModel::fetchAllHotels,
                    onNavigateToOptions = {
                        navController.navigate("accommodation_options")
                    }
                )
            }
        }

        // Nuevo Bounded Context conectado a la API
        composable("accommodation_options") {
            val optionsViewModel: OptionsViewModel = hiltViewModel()
            OptionsScreen(viewModel = optionsViewModel)
        }
    }
}
