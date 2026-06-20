package com.smartstay.application_mobile_frontend.feature.payments.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartstay.application_mobile_frontend.feature.payments.presentation.viewmodel.PaymentCheckoutUiState
import com.smartstay.application_mobile_frontend.feature.payments.presentation.viewmodel.PaymentCheckoutViewModel

@Composable
fun PaymentCheckoutDemoScreen(
    viewModel: PaymentCheckoutViewModel,
    onBack: () -> Unit,
    bookingId: Int? = null,
    hotelId: Int? = null,
    roomId: Int? = null,
    amount: Double? = null,
    hotelName: String? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(bookingId, hotelId, roomId, amount, hotelName) {
        viewModel.loadCheckoutDetails(
            bookingId = bookingId,
            hotelId = hotelId,
            roomId = roomId,
            amount = amount,
            hotelName = hotelName
        )
    }

    PaymentCheckoutContent(
        uiState = uiState,
        onBack = onBack,
        onCreateCheckout = viewModel::createMercadoPagoCheckout,
        onDecreaseNights = viewModel::decreaseNights,
        onIncreaseNights = viewModel::increaseNights,
        onOpenCheckout = uriHandler::openUri,
        modifier = modifier
    )
}

@Composable
private fun PaymentCheckoutContent(
    uiState: PaymentCheckoutUiState,
    onBack: () -> Unit,
    onCreateCheckout: () -> Unit,
    onDecreaseNights: () -> Unit,
    onIncreaseNights: () -> Unit,
    onOpenCheckout: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
                Text(
                    text = "Pago con Mercado Pago",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            BookingSummaryCard(
                uiState = uiState,
                onDecreaseNights = onDecreaseNights,
                onIncreaseNights = onIncreaseNights,
                canEditNights = uiState.checkoutUrl == null && !uiState.isLoading
            )

            uiState.checkoutUrl?.let { checkoutUrl ->
                MercadoPagoCheckoutCard(
                    checkoutUrl = checkoutUrl,
                    onOpenCheckout = onOpenCheckout
                )
            }

            uiState.errorMessage?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (uiState.checkoutUrl == null) {
                Button(
                    onClick = onCreateCheckout,
                    enabled = !uiState.isLoading && !uiState.isLoadingDetails,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.height(24.dp)
                        )
                    } else {
                        Text(text = "Proceder al pago")
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingSummaryCard(
    uiState: PaymentCheckoutUiState,
    onDecreaseNights: () -> Unit,
    onIncreaseNights: () -> Unit,
    canEditNights: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Nombre del hotel: ${uiState.hotelName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = "Numero de habitacion: ${uiState.roomId ?: "-"}")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Cantidad de noches: ${uiState.nights}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDecreaseNights,
                        enabled = canEditNights && uiState.nights > 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Reducir noches"
                        )
                    }
                    IconButton(
                        onClick = onIncreaseNights,
                        enabled = canEditNights && uiState.nights < 30
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar noches"
                        )
                    }
                }
            }
            Text(
                text = "Monto total: ${uiState.currency} ${"%.2f".format(uiState.totalAmount)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            if (uiState.isLoadingDetails) {
                Text(
                    text = "Cargando datos reales del backend...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MercadoPagoCheckoutCard(
    checkoutUrl: String,
    onOpenCheckout: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Checkout listo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { onOpenCheckout(checkoutUrl) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Abrir Mercado Pago")
            }
        }
    }
}
