// feature/iam/presentation/navigation/AppNavigation.kt
package com.smartstay.application_mobile_frontend.feature.iam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartstay.application_mobile_frontend.feature.iam.presentation.screens.SignInScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.screens.dashboards.*

/**
 * Main navigation graph setup.
 * Handles the routing logic and injects necessary ViewModels to top-level screen composables.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SIGN_IN
    ) {
        composable(AppRoutes.SIGN_IN) {
            SignInScreen(
                viewModel = hiltViewModel(),
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoutes.SIGN_IN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.CHAIN_ADMIN_DASHBOARD) {
            ChainAdminDashboard()
        }

        composable(AppRoutes.ADMIN_DASHBOARD) {
            AdminDashboard()
        }

        composable(AppRoutes.RECEPTION_DASHBOARD) {
            ReceptionDashboard()
        }

        composable(AppRoutes.HOUSEKEEPING_DASHBOARD) {
            HousekeepingDashboard()
        }

        composable(AppRoutes.MAINTENANCE_DASHBOARD) {
            MaintenanceDashboard()
        }

        composable(AppRoutes.STAFF_DASHBOARD) {
            StaffDashboard()
        }
    }
}