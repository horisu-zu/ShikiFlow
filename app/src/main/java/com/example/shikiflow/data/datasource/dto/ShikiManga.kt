package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiManga(
    val id: Int? = null,
    val name: String? = null,
    val russian: String? = null,
    val url: String? = null,
    @Serializable(with = ShikiImageSerializer::class)
    val image: ShikiImage? = null,
    val kind: String? = null,
    val score: String? = null,
    val status: String? = null,
    val volumes: Int? = null,
    val chapters: Int? = null,
    @SerialName("aired_on") val airedOn: String? = null,
    @SerialName("released_on") val releasedOn: String? = null
)