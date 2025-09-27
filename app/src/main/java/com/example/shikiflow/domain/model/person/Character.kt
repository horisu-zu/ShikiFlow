package com.example.shikiflow.domain.model.person

import com.example.shikiflow.domain.model.common.ShikiImage
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: Int,
    val image: ShikiImage,
    val name: String,
    val russian: String?,
    val url: String
)