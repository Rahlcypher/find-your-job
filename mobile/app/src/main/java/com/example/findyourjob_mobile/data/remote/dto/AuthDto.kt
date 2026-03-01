package com.example.findyourjob_mobile.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val roles: List<String> = listOf("CANDIDATE")
)

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String? = null,
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val roles: List<String>
)
