package com.example.shikiflow.domain.model.mangadex.manga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Description(
    val en: String,
    @SerialName("es-la")
    val esLa: String,
    val fr: String,
    @SerialName("pt-br")
    val ptBr: String,
    val uk: String
)