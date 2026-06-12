package com.smartstay.application_mobile_frontend.feature.iam.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smartstay.application_mobile_frontend.feature.iam.presentation.navigation.AppRoutes

/**
 * Transient loading screen displayed at cold start while the app determines
 * whether a valid local session exists.
 *
 * Resolves the start destination and forwards via [onReady] so the NavGraph
 * can pop this screen off the back stack before the user sees anything.
 *
 * @param isSignedIn True when [IamRepository.isSignedIn] found a non-expired token.
 * @param onReady    Callback invoked with the resolved destination route.
 *                   The NavGraph is responsible for navigating and clearing this screen.
 */
@Composable
fun SplashScreen(
    isSignedIn: Boolean,
    onReady:    (route: String) -> Unit
) {
    LaunchedEffect(isSignedIn) {
        // Redirect to SIGN_IN; the ViewModel will emit a Navigate event
        // with the correct role dashboard if a session was found.
        val destination = if (isSignedIn) AppRoutes.SIGN_IN else AppRoutes.SIGN_IN
        onReady(destination)
    }

    Box(
        modifier        = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}