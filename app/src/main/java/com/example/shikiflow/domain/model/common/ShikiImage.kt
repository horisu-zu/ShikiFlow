package com.example.shikiflow.domain.model.common

import kotlinx.serialization.Serializable

@Serializable
data class ShikiImage(
    val original: String? = null,
    val preview: String? = null,
    val x96: String? = null,
    val x48: String? = null
)
