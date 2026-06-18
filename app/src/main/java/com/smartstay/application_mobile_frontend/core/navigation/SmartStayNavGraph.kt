package com.smartstay.application_mobile_frontend.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartstay.application_mobile_frontend.feature.iam.data.local.SessionManager
import com.smartstay.application_mobile_frontend.feature.iam.presentation.screens.SignInScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel.IamViewModel
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.HotelListScreen
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.HotelListViewModel
import com.smartstay.application_mobile_frontend.feature.options.presentation.OptionsScreen
import com.smartstay.application_mobile_frontend.feature.options.presentation.OptionsViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val ACCOMMODATION_OPTIONS = "accommodation_options"
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SmartStayNavGraphEntryPoint {
    val sessionManager: SessionManager
}

@Composable
fun SmartStayNavGraph(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    val sessionManager: SessionManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SmartStayNavGraphEntryPoint::class.java
        ).sessionManager
    }

    val startDestination = if (!sessionManager.getToken().isNullOrEmpty()) Routes.DASHBOARD else Routes.LOGIN

    val iamViewModel: IamViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Routes.LOGIN) {
            SignInScreen(
                viewModel = iamViewModel,
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Routes.DASHBOARD) {
            val hotelListViewModel: HotelListViewModel = hiltViewModel()
            val uiState by hotelListViewModel.uiState.collectAsState()

            HotelListScreen(
                uiState = uiState,
                onRefresh = hotelListViewModel::fetchAllHotels,
                onNavigateToOptions = {
                    navController.navigate(Routes.ACCOMMODATION_OPTIONS)
                }
            )
        }

        composable(route = Routes.ACCOMMODATION_OPTIONS) {
            val optionsViewModel: OptionsViewModel = hiltViewModel()
            OptionsScreen(viewModel = optionsViewModel)
        }
    }
}
