package com.smartstay.application_mobile_frontend.feature.iam.domain.model

/**
 * Modelo de permisos del usuario autenticado.
 *
 * Dado el rol del actor autenticado, expone qué operaciones puede realizar
 * sobre otros usuarios del sistema. Replica en el frontend la lógica del
 * RoleAuthorizationService del backend, respetando la jerarquía de roles:
 *
 * - **ChainAdmin (3)**: acceso total. Puede crear, editar, asignar roles,
 *   desactivar y ver todos los usuarios.
 * - **HotelAdmin (2)**: puede gestionar roles inferiores al suyo
 *   (Recepcionista, Guest, Host), pero no a otros HotelAdmin ni ChainAdmin.
 * - **Recepcionista (1)**: no puede crear ni gestionar usuarios.
 * - **Guest (0) / Host (0)**: solo pueden ver su propio perfil.
 *
 * @property actorRole Rol del usuario autenticado, obtenido desde el token JWT.
 */
data class UserPermissions(val actorRole: String) {

    // ──────────────────────────────────────────────
    // Nivel jerárquico
    // ──────────────────────────────────────────────

    /**
     * Nivel del actor según la jerarquía de roles definida en [RoleHierarchy].
     *
     * Retorna -1 si el rol no está registrado en la jerarquía (rol desconocido).
     */
    val level: Int
        get() = RoleHierarchy.hierarchy[actorRole] ?: -1

    // ──────────────────────────────────────────────
    // Permisos de lectura
    // ──────────────────────────────────────────────

    /**
     * Indica si el actor puede crear nuevos usuarios en el sistema.
     *
     * **Regla de negocio**: solo [ChainAdmin] tiene privilegios de creación.
     */
    val canCreateUsers: Boolean
        get() = actorRole == "chain_admin"

    /**
     * Indica si el actor puede gestionar usuarios (ver y modificar dentro de su alcance).
     *
     * **Regla de negocio**: [ChainAdmin] y [HotelAdmin] pueden gestionar usuarios.
     * [Recepcionista], [Guest] y [Host] no tienen capacidad de gestión.
     */
    val canManageUsers: Boolean
        get() = actorRole == "chain_admin" || actorRole == "admin" || actorRole == "hotel_admin"

    /**
     * Indica si el actor puede ver la lista completa de usuarios.
     *
     * **Regla de negocio**: [ChainAdmin] y [HotelAdmin] pueden ver todos los usuarios.
     * [Recepcionista], [Guest] y [Host] solo pueden ver su propio perfil.
     */
    val canViewAllUsers: Boolean
        get() = actorRole == "chain_admin" || actorRole == "admin" || actorRole == "hotel_admin"

    /**
     * Indica si el actor puede gestionar propiedades (hoteles, habitaciones).
     *
     * **Regla de negocio**: administradores corporativos y dueños de hotel (Hosts).
     */
    val canManageProperties: Boolean
        get() = actorRole == "chain_admin" || actorRole == "admin" || actorRole == "host" || actorRole == "hotel_admin"

    // ──────────────────────────────────────────────
    // Permisos condicionados al rol del objetivo
    // ──────────────────────────────────────────────

    /**
     * Determina si el actor puede editar al usuario con el rol [targetRole].
     *
     * **Regla de negocio**:
     * - [ChainAdmin] puede editar a cualquier usuario.
     * - [HotelAdmin] puede editar solo a usuarios de nivel inferior al suyo
     *   ([Recepcionista], [Guest], [Host]). No puede editar a otro [HotelAdmin]
     *   ni a un [ChainAdmin].
     * - El resto de roles no pueden editar a nadie.
     *
     * @param targetRole Rol del usuario que se desea editar.
     * @return `true` si el actor tiene permiso para editar al usuario objetivo.
     */
    fun canEditUser(targetRole: String): Boolean {
        // ChainAdmin tiene acceso total
        if (actorRole == "chain_admin") return true
        // HotelAdmin solo puede editar roles de nivel inferior al suyo
        if (actorRole == "hotel_admin") {
            val targetLevel = RoleHierarchy.hierarchy[targetRole] ?: -1
            return targetLevel < level
        }
        return false
    }

    /**
     * Determina si el actor puede abrir el diálogo de asignación de rol para
     * el usuario con rol actual [targetCurrentRole].
     *
     * **Regla de negocio**: las mismas restricciones que [canEditUser].
     * El diálogo de asignación ya se encarga de filtrar los roles concretos
     * que el actor puede otorgar mediante [RoleHierarchy.getAssignableRoles];
     * aquí solo se verifica si el actor puede siquiera iniciar el flujo.
     *
     * @param targetCurrentRole Rol actual del usuario objetivo.
     * @return `true` si el actor puede asignar un nuevo rol al usuario objetivo.
     */
    fun canAssignRole(targetCurrentRole: String): Boolean {
        return canEditUser(targetCurrentRole)
    }

    /**
     * Determina si el actor puede desactivar al usuario con el rol [targetRole].
     *
     * **Regla de negocio**: las mismas restricciones que [canEditUser].
     * La protección del último [ChainAdmin] activo (no se puede desactivar
     * al último administrador de cadena) se delega al backend; el frontend
     * concede la acción si el actor tiene permiso de edición sobre el objetivo.
     *
     * @param targetRole Rol del usuario que se desea desactivar.
     * @return `true` si el actor puede desactivar al usuario objetivo.
     */
    fun canDeactivateUser(targetRole: String): Boolean {
        // La protección del último ChainAdmin la aplica el backend
        return canEditUser(targetRole)
    }
}
