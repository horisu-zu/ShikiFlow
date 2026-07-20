package com.example.shikiflow.data.datasource.dto.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiImageUploadResponse(
    val id: Int,
    val preview: String,
    val url: String,
    @SerialName("bbcode") val bbCode: String
)
