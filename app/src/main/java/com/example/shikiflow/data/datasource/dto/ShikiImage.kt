package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShikiImage(
    val original: String? = null,
    val preview: String? = null,
    val x96: String? = null,
    val x48: String? = null
)
