package com.smartstay.application_mobile_frontend.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Indicador de carga genérico con un [CircularProgressIndicator] centrado
 * y un mensaje opcional debajo.
 *
 * Uso típico:
 * ```
 * LoadingIndicator(message = "Cargando usuarios...")
 * ```
 *
 * @param modifier Modificador para personalizar el contenedor.
 * @param message Texto opcional mostrado debajo del spinner (nulo para ocultarlo).
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String? = "Cargando..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()

            if (message != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Vista de estado vacío con un icono, un mensaje descriptivo
 * y un botón de acción opcional.
 *
 * Uso típico:
 * ```
 * EmptyStateView(
 *     message = "No hay usuarios registrados.",
 *     actionLabel = "Recargar",
 *     onAction = { viewModel.loadUsers() }
 * )
 * ```
 *
 * @param modifier Modificador para personalizar el contenedor.
 * @param message Mensaje informativo sobre el estado vacío.
 * @param icon Icono ilustrativo (por defecto [Icons.Filled.Inbox]).
 * @param actionLabel Texto opcional del botón de acción (nulo para ocultarlo).
 * @param onAction Acción ejecutada al pulsar el botón (nulo para ocultarlo).
 */
@Composable
fun EmptyStateView(
    modifier: Modifier = Modifier,
    message: String,
    icon: ImageVector = Icons.Filled.Inbox,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAction) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

/**
 * Vista de error genérica con un icono de error, el mensaje del error
 * y un botón opcional para reintentar.
 *
 * Uso típico:
 * ```
 * ErrorView(
 *     message = "No se pudieron cargar los datos.",
 *     onRetry = { viewModel.loadUsers() }
 * )
 * ```
 *
 * @param modifier Modificador para personalizar el contenedor.
 * @param message Mensaje descriptivo del error ocurrido.
 * @param onRetry Acción ejecutada al pulsar "Reintentar" (nulo para ocultar el botón).
 */
@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text(text = "Reintentar")
                }
            }
        }
    }
}
