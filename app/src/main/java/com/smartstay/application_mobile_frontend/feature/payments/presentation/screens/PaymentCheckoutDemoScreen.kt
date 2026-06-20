package com.smartstay.application_mobile_frontend.feature.payments.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        onOpenCheckout = uriHandler::openUri,
        onCheckoutOpened = viewModel::markCheckoutOpened,
        onConfirmPayment = viewModel::confirmPaymentManually,
        modifier = modifier
    )
}

@Composable
private fun PaymentCheckoutContent(
    uiState: PaymentCheckoutUiState,
    onBack: () -> Unit,
    onCreateCheckout: () -> Unit,
    onOpenCheckout: (String) -> Unit,
    onCheckoutOpened: () -> Unit,
    onConfirmPayment: () -> Unit,
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Pago con Mercado Pago",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            BookingSummaryCard(uiState = uiState)
            MercadoPagoMethodCard()

            uiState.checkoutUrl?.let { checkoutUrl ->
                MercadoPagoCheckoutCard(
                    checkoutUrl = checkoutUrl,
                    onOpenCheckout = onOpenCheckout,
                    onCheckoutOpened = onCheckoutOpened,
                    onConfirmPayment = onConfirmPayment,
                    hasOpenedCheckout = uiState.hasOpenedCheckout,
                    isPaymentConfirmed = uiState.isPaymentConfirmed
                )
            }

            if (uiState.isPaymentConfirmed) {
                PaymentConfirmedCard(uiState = uiState)
            }

            uiState.errorMessage?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

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
                    Text(text = "Crear checkout ${uiState.currency} ${"%.2f".format(uiState.amount)}")
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver")
            }
        }
    }
}

@Composable
private fun BookingSummaryCard(uiState: PaymentCheckoutUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = uiState.hotelName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            uiState.hotelLocation?.let { location ->
                Text(text = location)
            }
            uiState.bookingId?.let { bookingId ->
                Text(text = "Reserva #$bookingId")
            }
            uiState.hotelId?.let { hotelId ->
                Text(text = "Hotel ID: $hotelId")
            }
            uiState.roomId?.let { roomId ->
                Text(text = "Habitacion ID: $roomId")
            }
            uiState.roomTypeName?.let { roomTypeName ->
                Text(text = "Tipo: $roomTypeName")
            }
            Text(text = "${uiState.nights} noches")
            Text(
                text = "Total: ${uiState.currency} ${"%.2f".format(uiState.amount)}",
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
private fun MercadoPagoMethodCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Metodo de pago",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Mercado Pago",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MercadoPagoCheckoutCard(
    checkoutUrl: String,
    onOpenCheckout: (String) -> Unit,
    onCheckoutOpened: () -> Unit,
    onConfirmPayment: () -> Unit,
    hasOpenedCheckout: Boolean,
    isPaymentConfirmed: Boolean
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
                onClick = {
                    onOpenCheckout(checkoutUrl)
                    onCheckoutOpened()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Abrir Mercado Pago")
            }
            if (hasOpenedCheckout) {
                Button(
                    onClick = onConfirmPayment,
                    enabled = !isPaymentConfirmed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isPaymentConfirmed) "Pago confirmado" else "Confirmar pago realizado")
                }
            }
        }
    }
}

@Composable
private fun PaymentConfirmedCard(uiState: PaymentCheckoutUiState) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Pago completado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Monto pagado: ${uiState.currency} ${"%.2f".format(uiState.amount)}")
            uiState.roomId?.let { roomId ->
                Text(text = "Habitacion ID: $roomId")
            }
        }
    }
}
