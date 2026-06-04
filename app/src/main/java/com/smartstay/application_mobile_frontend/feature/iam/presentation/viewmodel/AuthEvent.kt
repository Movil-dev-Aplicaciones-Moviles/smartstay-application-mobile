// feature/iam/presentation/viewmodel/AuthEvent.kt
package com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel

/**
 * Events sent from the ViewModel to the UI.
 */
sealed interface AuthEvent {
    data class Navigate(val route: String) : AuthEvent
    data class Error(val message: String) : AuthEvent
}