// feature/iam/presentation/navigation/RoleDestinationResolver.kt
package com.smartstay.application_mobile_frontend.feature.iam.presentation.navigation

import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserRole

/**
 * Domain service responsible for mapping a [UserRole] to its corresponding UI entry point.
 */
object RoleDestinationResolver {

    /**
     * Resolves the navigation route based on the provided role.
     * * @param role The authenticated user's role.
     * @return The specific route string for the dashboard.
     * @throws IllegalArgumentException if a restricted role (e.g., GUEST) attempts to access internal routes.
     */
    fun resolve(role: UserRole): String {
        return when (role) {
            UserRole.CHAIN_ADMIN -> AppRoutes.CHAIN_ADMIN_DASHBOARD
            UserRole.ADMIN -> AppRoutes.ADMIN_DASHBOARD
            UserRole.RECEPTION -> AppRoutes.RECEPTION_DASHBOARD
            UserRole.HOUSEKEEPING -> AppRoutes.HOUSEKEEPING_DASHBOARD
            UserRole.MAINTENANCE -> AppRoutes.MAINTENANCE_DASHBOARD
            UserRole.STAFF -> AppRoutes.STAFF_DASHBOARD
            //TODO: We need to fix this and replace with a permission.
            UserRole.GUEST -> throw IllegalArgumentException("Guests are not permitted to access operational dashboards.")
        }
    }
}