package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShikiImageUrls(
    val original: String,
    val preview: String,
    val x96: String,
    val x48: String
)