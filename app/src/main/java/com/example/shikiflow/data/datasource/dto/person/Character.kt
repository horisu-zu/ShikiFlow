package com.example.shikiflow.data.datasource.dto.person

import com.example.shikiflow.data.datasource.dto.ShikiImage
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: Int,
    val image: ShikiImage,
    val name: String,
    val russian: String?,
    val url: String
)