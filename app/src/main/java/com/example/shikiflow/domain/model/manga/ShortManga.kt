package com.example.shikiflow.domain.model.manga

import com.example.shikiflow.domain.model.common.ShikiImage
import kotlinx.serialization.Serializable

@Serializable
data class ShortManga(
    val id: Long,
    val name: String,
    val kind: String,
    val image: ShikiImage
)
