// feature/iam/presentation/navigation/AppRoutes.kt
package com.smartstay.application_mobile_frontend.feature.iam.presentation.navigation

/**
 * Centralized routing dictionary for the application navigation graph.
 * Ensures type safety and avoids hardcoded strings across composables.
 */
object AppRoutes {
    const val SPLASH = "splash"
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"
    const val CHAIN_ADMIN_DASHBOARD = "chain_admin_dashboard"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val RECEPTION_DASHBOARD = "reception_dashboard"
    const val HOUSEKEEPING_DASHBOARD = "housekeeping_dashboard"
    const val MAINTENANCE_DASHBOARD = "maintenance_dashboard"
    const val STAFF_DASHBOARD = "staff_dashboard"
}