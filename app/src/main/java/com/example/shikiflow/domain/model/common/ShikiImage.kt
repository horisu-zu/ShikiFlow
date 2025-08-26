package com.example.shikiflow.domain.model.common

import kotlinx.serialization.Serializable

@Serializable
data class ShikiImage(
    val original: String?,
    val preview: String?,
    val x96: String?,
    val x48: String?
)
