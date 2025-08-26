package com.example.shikiflow.domain.model.mangadex.manga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AltTitle(
    val de: String? = null,
    val en: String? = null,
    val es: String? = null,
    @SerialName("es-la")
    val esLa: String? = null,
    val fr: String? = null,
    val hu: String? = null,
    val it: String? = null,
    val ja: String? = null,
    @SerialName("ja-ro")
    val jaRo: String? = null,
    val ko: String? = null,
    val pl: String? = null,
    @SerialName("pt-br")
    val ptBr: String? = null,
    val th: String? = null,
    val uk: String? = null,
    val vi: String? = null,
    val zh: String? = null,
    @SerialName("zh-hk")
    val zhHk: String? = null
)