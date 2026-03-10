package com.example.shikiflow.data.datasource.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String?,
    @SerialName("refresh_token") val refreshToken: String? = null
)