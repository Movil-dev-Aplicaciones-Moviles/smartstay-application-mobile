package com.smartstay.application_mobile_frontend.feature.payments.data.mapper

import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.CreatePaymentRequestDto
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.PaymentCheckoutDto
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.PaymentDto
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.CreatePaymentCommand
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.Payment
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.PaymentCheckout
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.PaymentMethod
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.PaymentProvider
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.PaymentStatus
import java.time.LocalDateTime

fun CreatePaymentCommand.toDto(): CreatePaymentRequestDto {
    return CreatePaymentRequestDto(
        bookingId = bookingId,
        amount = amount,
        currency = currency,
        method = method.name,
        provider = provider.name,
        cardHolderName = cardHolderName,
        cardToken = cardToken
    )
}

fun PaymentDto.toDomain(): Payment {
    return Payment(
        id = id,
        bookingId = bookingId,
        transactionId = transactionId.orEmpty(),
        amount = amount,
        currency = currency ?: "PEN",
        method = method.toEnumOrDefault(PaymentMethod.CREDIT_CARD),
        provider = provider.toEnumOrDefault(PaymentProvider.MOCK),
        cardHolderName = cardHolderName,
        cardNumberMasked = cardNumberMasked,
        paymentDate = paymentDate?.toLocalDateTimeOrNull(),
        status = status.toEnumOrDefault(PaymentStatus.PENDING)
    )
}

fun PaymentCheckoutDto.toDomain(): PaymentCheckout {
    return PaymentCheckout(
        paymentId = paymentId,
        bookingId = bookingId,
        provider = provider.toEnumOrDefault(PaymentProvider.MERCADO_PAGO),
        checkoutUrl = checkoutUrl.orEmpty(),
        status = status.toEnumOrDefault(PaymentStatus.PENDING)
    )
}

private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T {
    return this?.let { value ->
        enumValues<T>().firstOrNull { it.name.equals(value, ignoreCase = true) }
    } ?: default
}

private fun String.toLocalDateTimeOrNull(): LocalDateTime? {
    return runCatching { LocalDateTime.parse(this) }.getOrNull()
}
