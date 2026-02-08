package com.example.shikiflow.domain.model.auth

data class AuthCredentials(
    val accessToken: String?,
    val refreshToken: String?
)
