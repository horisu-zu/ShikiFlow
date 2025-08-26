package com.example.shikiflow.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class ImageUrls(
    val original: String,
    val preview: String,
    val x96: String,
    val x48: String
)
