package com.example.shikiflow.data.auth

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String?,
    @SerialName("refresh_token") val refreshToken: String?
) {
    val isEmpty get() = accessToken == null || refreshToken == null
}
