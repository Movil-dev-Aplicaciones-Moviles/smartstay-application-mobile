package com.smartstay.application_mobile_frontend.feature.iam.domain.model

/**
 * Value Object representing the distinct operational roles within the SmartStay platform.
 *
 * Determines the scope of access and the specific dashboard a user will see after sign-in.
 * The [value] string must match exactly what the backend encodes inside the JWT payload.
 *
 * Hierarchy (broadest → narrowest access):
 *   CHAIN_ADMIN → ADMIN → RECEPTION / HOUSEKEEPING / MAINTENANCE → STAFF → GUEST
 *
 * @property value Canonical string representation, matching the backend role claim.
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

    /** External user / client with no internal operational access. */
    GUEST("guest");

    companion object {

        /**
         * Safely parses a raw string into a [UserRole].
         *
         * Comparison is case-insensitive and trims surrounding whitespace,
         * so "Admin", "ADMIN", and " admin " all resolve to [ADMIN].
         *
         * @param role Raw role string, typically extracted from the JWT payload.
         * @return The matching [UserRole], or [GUEST] if the value is null, blank,
         *         or does not match any known role.
         */
        fun from(role: String?): UserRole {
            if (role.isNullOrBlank()) return GUEST
            return entries.firstOrNull {
                it.value.equals(role.trim(), ignoreCase = true)
            } ?: GUEST
        }
    }
}