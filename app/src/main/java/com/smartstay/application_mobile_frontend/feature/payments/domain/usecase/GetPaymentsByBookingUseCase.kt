package com.smartstay.application_mobile_frontend.feature.payments.domain.usecase

import com.smartstay.application_mobile_frontend.feature.payments.domain.model.Payment
import com.smartstay.application_mobile_frontend.feature.payments.domain.repository.PaymentRepository
import javax.inject.Inject

class GetPaymentsByBookingUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(bookingId: Int): Result<List<Payment>> {
        return repository.getPaymentsByBookingId(bookingId)
    }
}
