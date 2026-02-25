package com.example.shikiflow.data.datasource.dto.mangadex.manga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Title(
    val en: String? = null,
    @SerialName("ja-ro") val jaRo: String? = null
)