// feature/iam/domain/model/UserRole.kt
package com.smartstay.application_mobile_frontend.feature.iam.domain.model

/**
 * Value Object representing the distinct operational roles within the SmartStay platform.
 * Determines the scope of access and the specific dashboard a user will see.
 *
 * @property value The string representation of the role matching the backend resource.
 */
enum class UserRole(val value: String) {
    /** Global administrator managing the entire hotel chain. */
    CHAIN_ADMIN("chain_admin"),

    /** Local administrator managing a single specific hotel. */
    ADMIN("admin"),

    /** Staff member handling check-ins, check-outs, and guest services. */
    RECEPTION("reception"),

    /** Staff member responsible for room cleaning and inventory. */
    HOUSEKEEPING("housekeeping"),

    /** Staff member handling repairs and facility operations. */
    MAINTENANCE("maintenance"),

    /** General staff member with basic internal access. */
    STAFF("staff"),

    /** External user/client with no internal operational access. */
    GUEST("guest");

    companion object {
        /**
         * Safely parses a string into a [UserRole]. Defaults to [GUEST] if unrecognized.
         */
        fun from(role: String?): UserRole {
            return entries.firstOrNull {
                it.value.equals(role, ignoreCase = true)
            } ?: GUEST
        }
    }
}