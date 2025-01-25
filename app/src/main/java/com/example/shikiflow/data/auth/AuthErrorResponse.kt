package com.example.shikiflow.data.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthErrorResponse(
    @SerialName("error") val error: String,
    @SerialName("error_description") val errorDescription: String,
    @SerialName("state") val state: String
)
