package com.example.shikiflow.domain.model.manga

import kotlinx.serialization.Serializable

@Serializable
data class ShortManga(
    val id: Long,
    val name: String,
    val kind: String
)
