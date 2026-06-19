package com.smartstay.application_mobile_frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import com.smartstay.application_mobile_frontend.core.navigation.SmartStayNavGraph
import com.smartstay.application_mobile_frontend.ui.theme.ApplicationmobilefrontendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val paymentReturnData = mutableStateOf<PaymentReturnData?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentReturnData.value = intent.toPaymentReturnData()
        enableEdgeToEdge()
        setContent {
            ApplicationmobilefrontendTheme {
                val navController = rememberNavController()
                SmartStayNavGraph(
                    navController = navController,
                    paymentReturnData = paymentReturnData.value,
                    onPaymentReturnConsumed = {
                        paymentReturnData.value = null
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        paymentReturnData.value = intent.toPaymentReturnData()
    }
}

data class PaymentReturnData(
    val status: String,
    val bookingId: Int?,
    val amount: Double?
)

private fun Intent?.toPaymentReturnData(): PaymentReturnData? {
    val uri = this?.data ?: return null
    if (uri.scheme != "smartstay" || uri.host != "payment") return null

    val status = uri.pathSegments.firstOrNull()
        ?.takeIf { it in setOf("success", "failure", "pending") }
        ?: return null

    return PaymentReturnData(
        status = status,
        bookingId = uri.getQueryParameter("bookingId")?.toIntOrNull()?.takeIf { it > 0 },
        amount = uri.getQueryParameter("amount")?.toDoubleOrNull()?.takeIf { it > 0.0 }
    )
}
