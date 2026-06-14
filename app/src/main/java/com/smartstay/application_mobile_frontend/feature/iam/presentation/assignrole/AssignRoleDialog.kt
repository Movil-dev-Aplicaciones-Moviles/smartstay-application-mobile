package com.smartstay.application_mobile_frontend.feature.iam.presentation.assignrole

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.smartstay.application_mobile_frontend.R
import androidx.compose.ui.unit.dp
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.RoleHierarchy

/**
 * Diálogo de asignación de rol para la gestión de usuarios (IAM).
 *
 * Muestra una lista de roles asignables según la jerarquía del sistema,
 * filtrando aquellos que el actor autenticado puede otorgar.
 * La selección es inmediata: al pulsar un rol se invoca [onAssignRole].
 * El diálogo no se cierra automáticamente; será la pantalla padre quien
 * gestione la navegación tras el éxito de la operación.
 *
 * @param actorRole Rol del usuario autenticado (obtenido desde TokenManager).
 * @param targetUserId ID del usuario al que se le asignará el nuevo rol.
 * @param targetCurrentRole Rol actual del usuario objetivo.
 * @param onDismiss Callback invocado al pulsar "Cancelar".
 * @param onAssignRole Callback invocado con el nombre del rol seleccionado.
 */
@Composable
fun AssignRoleDialog(
    actorRole: String,
    targetUserId: Int,
    targetCurrentRole: String,
    onDismiss: () -> Unit,
    onAssignRole: (role: String) -> Unit
) {
    val assignableRoles = remember(actorRole, targetCurrentRole) {
        RoleHierarchy.getAssignableRoles(actorRole, targetCurrentRole)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.assign_role_dialog_title))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (assignableRoles.isEmpty()) {
                    Text(
                        text = stringResource(R.string.assign_role_dialog_no_roles),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    assignableRoles.forEach { role ->
                        RoleItem(
                            role = role,
                            onClick = { onAssignRole(role) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        },
        confirmButton = {}, // No se usa: la selección es directa al pulsar un rol
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.assign_role_dialog_cancel))
            }
        }
    )
}

/**
 * Elemento individual de la lista de roles asignables.
 *
 * Muestra el nombre del rol con un icono [ManageAccounts] en una superficie
 * clickeable, permitiendo al usuario seleccionar el rol deseado.
 *
 * @param role Nombre del rol a mostrar.
 * @param onClick Acción al pulsar sobre el elemento.
 */
@Composable
private fun RoleItem(
    role: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ManageAccounts,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = role,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
