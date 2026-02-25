package com.example.shikiflow.data.datasource.dto.mangadex.cover

import kotlinx.serialization.Serializable

@Serializable
data class CoverAttributes(
    val createdAt: String,
    val description: String,
    val fileName: String,
    val locale: String,
    val updatedAt: String,
    val version: Int,
    val volume: String? = null
)