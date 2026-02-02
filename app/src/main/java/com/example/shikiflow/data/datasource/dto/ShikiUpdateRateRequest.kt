package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShikiUpdateRateRequest(
    val chapters: Int? = null,
    val episodes: Int? = null,
    val rewatches: Int? = null,
    val score: Int? = null,
    val status: String? = null,
    val text: String? = null,
    val volumes: Int? = null
)