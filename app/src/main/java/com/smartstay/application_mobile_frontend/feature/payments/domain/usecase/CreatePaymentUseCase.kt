package com.smartstay.application_mobile_frontend.feature.payments.domain.usecase

import com.smartstay.application_mobile_frontend.feature.payments.domain.model.CreatePaymentCommand
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.Payment
import com.smartstay.application_mobile_frontend.feature.payments.domain.repository.PaymentRepository
import javax.inject.Inject

class CreatePaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(command: CreatePaymentCommand): Result<Payment> {
        return repository.createPayment(command)
    }
}
