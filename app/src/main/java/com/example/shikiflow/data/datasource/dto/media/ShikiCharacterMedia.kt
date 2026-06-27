package com.example.shikiflow.data.datasource.dto.media

import com.example.shikiflow.data.datasource.dto.ShikiImage
import com.example.shikiflow.data.datasource.dto.ShikiImageSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ShikiCharacterMedia(
    val id: Int? = null,
    val name: String? = null,
    val russian: String? = null,
    @Serializable(with = ShikiImageSerializer::class)
    val image: ShikiImage? = null,
    val role: String? = null
)
