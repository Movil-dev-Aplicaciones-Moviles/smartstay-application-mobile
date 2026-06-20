package com.smartstay.application_mobile_frontend.feature.iam.domain.model

/**
 * Jerarquía de roles del sistema SmartStay.
 *
 * Define los niveles de autoridad de cada rol y expone una función
 * para obtener los roles que un actor autenticado puede asignar a otro usuario,
 * respetando la regla: solo se pueden asignar roles de nivel estrictamente
 * inferior al propio, y no se puede reasignar el mismo rol que ya posee el objetivo.
 */
object RoleHierarchy {

    /**
     * Mapa de roles a su nivel jerárquico.
     * A mayor número, mayor autoridad.
     */
    val hierarchy: Map<String, Int> = mapOf(
        "chain_admin" to 3,
        "admin" to 2,
        "reception" to 1,
        "staff" to 1,
        "housekeeping" to 1,
        "maintenance" to 1,
        "guest" to 0
    )

    /**
     * Retorna la lista de roles que el [actorRole] puede asignar al usuario
     * cuyo rol actual es [targetCurrentRole].
     *
     * Filtra los roles de nivel estrictamente inferior al del actor y
     * excluye el rol actual del usuario objetivo.
     *
     * @param actorRole Rol del usuario autenticado que realiza la asignación.
     * @param targetCurrentRole Rol actual del usuario al que se le cambiará el rol.
     * @return Lista de nombres de rol asignables, ordenados de mayor a menor nivel.
     */
    fun getAssignableRoles(actorRole: String, targetCurrentRole: String): List<String> {
        val actorLevel = hierarchy[actorRole] ?: -1
        return hierarchy
            .filter { (role, level) ->
                level < actorLevel && role != targetCurrentRole
            }
            .entries
            .sortedByDescending { it.value }
            .map { it.key }
    }
}
