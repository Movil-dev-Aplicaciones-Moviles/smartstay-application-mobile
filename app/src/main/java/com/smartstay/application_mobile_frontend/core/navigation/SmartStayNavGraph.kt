// core/navigation/SmartStayNavGraph.kt
package com.smartstay.application_mobile_frontend.core.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartstay.application_mobile_frontend.feature.iam.presentation.screens.SignInScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel.IamViewModel

@Composable
fun SmartStayNavGraph() {
    val navController = rememberNavController()

    val iamViewModel: IamViewModel = hiltViewModel()
    val uiState by iamViewModel.uiState.collectAsState()

    val startDestination = if (uiState.isSignedIn) "dashboard" else "login"

    LaunchedEffect(uiState.isSignedIn, uiState.role) {
        if (uiState.isSignedIn) {
            Log.d("SmartStay_E2E", "=======================================================")
            Log.d("SmartStay_E2E", "✓ INTERCEPTOR: Autenticación verificada en pipeline móvil.")
            Log.d("SmartStay_E2E", "🛡️ ROL DETECTADO DESDE EL JWT: [${uiState.role.uppercase()}]")
            Log.d("SmartStay_E2E", "=======================================================")
        }
    }

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
                // Dashboard implementation belongs to the presentation layer of the dashboards feature.
            }
        }
    }
}
