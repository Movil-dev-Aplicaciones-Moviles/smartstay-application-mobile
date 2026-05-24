// feature/iam/domain/model/IamModels.kt
package com.smartstay.application_mobile_frontend.feature.iam.domain.model

data class User(
    val id: Int,
    val username: String,
    val role: String,
    val roles: List<String>
)

data class SignInCommand(val username: String, val password: String)
data class SignUpCommand(val username: String, val password: String, val role: String? = null, val name: String? = null)