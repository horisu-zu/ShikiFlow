package com.example.shikiflow.data.datasource.dto.person

import com.example.shikiflow.data.datasource.dto.ShikiAnime
import com.example.shikiflow.data.datasource.dto.ShikiManga
import kotlinx.serialization.Serializable

@Serializable
data class Work(
    val anime: ShikiAnime?,
    val manga: ShikiManga?,
    val role: String
)