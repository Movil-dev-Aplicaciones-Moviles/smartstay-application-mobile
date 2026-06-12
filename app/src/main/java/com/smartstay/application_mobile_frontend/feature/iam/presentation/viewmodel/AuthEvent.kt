package com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel

/**
 * One-shot events emitted by [IamViewModel] via a [Channel] and consumed
 * exactly once by the UI layer.
 *
 * Using a sealed interface + Channel (instead of StateFlow) guarantees that
 * navigation and error events are not re-triggered on recomposition or
 * configuration changes.
 */
sealed interface AuthEvent {

    /**
     * Instructs the UI to navigate to [route], clearing the back stack as needed.
     *
     * @param route One of the constants defined in [AppRoutes].
     */
    data class Navigate(val route: String) : AuthEvent

    /**
     * Signals a non-fatal error that the UI should surface to the user
     * (e.g. via a Snackbar or inline error text).
     *
     * @param message Human-readable description of the error.
     */
    data class Error(val message: String) : AuthEvent
}