package com.smartstay.application_mobile_frontend.feature.payments.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartstay.application_mobile_frontend.feature.payments.presentation.viewmodel.PaymentReturnViewModel

@Composable
fun PaymentReturnScreen(
    status: String,
    bookingId: Int?,
    amount: Double?,
    viewModel: PaymentReturnViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = status.toPaymentResult()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(status, bookingId, amount) {
        viewModel.registerSuccessfulPayment(
            status = status,
            bookingId = bookingId,
            amount = amount
        )
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = result.containerColor()
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (uiState.isRegisteringPayment) {
                        CircularProgressIndicator()
                        Text(
                            text = "Registrando pago en backend...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    uiState.backendMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    uiState.backendError?.let { error ->
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Volver")
                    }
                }
            }
        }
    }
}

private data class PaymentResultContent(
    val title: String,
    val message: String,
    val tone: PaymentResultTone
)

private enum class PaymentResultTone {
    Success,
    Failure,
    Pending
}

private fun String.toPaymentResult(): PaymentResultContent {
    return when (lowercase()) {
        "success" -> PaymentResultContent(
            title = "Pago aprobado",
            message = "Mercado Pago retorno un resultado aprobado.",
            tone = PaymentResultTone.Success
        )
        "failure" -> PaymentResultContent(
            title = "Pago rechazado",
            message = "Mercado Pago retorno un resultado rechazado.",
            tone = PaymentResultTone.Failure
        )
        "pending" -> PaymentResultContent(
            title = "Pago pendiente",
            message = "Mercado Pago retorno un resultado pendiente.",
            tone = PaymentResultTone.Pending
        )
        else -> PaymentResultContent(
            title = "Estado de pago",
            message = "No se pudo identificar el resultado del pago.",
            tone = PaymentResultTone.Pending
        )
    }
}

@Composable
private fun PaymentResultContent.containerColor() = when (tone) {
    PaymentResultTone.Success -> MaterialTheme.colorScheme.primaryContainer
    PaymentResultTone.Failure -> MaterialTheme.colorScheme.errorContainer
    PaymentResultTone.Pending -> MaterialTheme.colorScheme.secondaryContainer
}
