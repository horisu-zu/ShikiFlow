package com.example.shikiflow.data.datasource.dto.media

import com.example.shikiflow.data.datasource.dto.ShikiImage
import kotlinx.serialization.Serializable

@Serializable
sealed class ShikiShortMedia {
    abstract val id: Long
    abstract val name: String
    abstract val russian: String
    abstract val image: ShikiImage
}