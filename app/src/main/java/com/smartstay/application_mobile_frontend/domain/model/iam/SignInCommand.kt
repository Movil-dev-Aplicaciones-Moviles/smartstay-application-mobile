package com.smartstay.application_mobile_frontend.domain.model.iam

data class SignInCommand(
    val username: String,
    val password: String
)