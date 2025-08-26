package com.example.shikiflow.domain.model.mangadex.manga

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Description(
    val en: String,
    @SerializedName("es-la")
    val esLa: String,
    val fr: String,
    @SerializedName("pt-br")
    val ptBr: String,
    val uk: String
)