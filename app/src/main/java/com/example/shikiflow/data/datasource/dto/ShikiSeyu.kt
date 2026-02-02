package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShikiSeyu(
    val id: Int,
    val name: String,
    val russian: String?,
    val image: ShikiImage?,
    val url: String?
)
