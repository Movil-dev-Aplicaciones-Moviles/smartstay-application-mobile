package com.smartstay.application_mobile_frontend.feature.payments.domain.usecase

import com.smartstay.application_mobile_frontend.feature.payments.domain.model.CreatePaymentCommand
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.PaymentCheckout
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.PaymentProvider
import com.smartstay.application_mobile_frontend.feature.payments.domain.repository.PaymentRepository
import javax.inject.Inject

class CreateMercadoPagoCheckoutUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(command: CreatePaymentCommand): Result<PaymentCheckout> {
        return repository.createMercadoPagoCheckout(
            command.copy(provider = PaymentProvider.MERCADO_PAGO)
        )
    }
}
