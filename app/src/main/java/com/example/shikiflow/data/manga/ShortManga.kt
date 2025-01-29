package com.example.shikiflow.data.manga

import kotlinx.serialization.Serializable

@Serializable
data class ShortManga(
    val id: Long,
    val name: String,
    val kind: String
)
