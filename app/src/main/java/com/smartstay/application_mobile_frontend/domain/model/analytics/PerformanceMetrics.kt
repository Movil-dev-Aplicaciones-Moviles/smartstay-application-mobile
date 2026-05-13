package com.smartstay.application_mobile_frontend.domain.model.analytics

import java.time.LocalDateTime

data class PerformanceMetrics(
    val totalRevenue: Double,
    val totalBookings: Int,
    val occupancyRate: Double,
    val cancelledBookings: Int,
    val generatedAt: LocalDateTime
)