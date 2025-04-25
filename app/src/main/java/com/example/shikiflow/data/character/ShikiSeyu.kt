package com.example.shikiflow.data.character

import com.example.shikiflow.data.common.ShikiImage
import kotlinx.serialization.Serializable

@Serializable
data class ShikiSeyu(
    val id: Int?,
    val name: String?,
    val russian: String?,
    val image: ShikiImage?,
    val url: String?
)
