package com.example.shikiflow.domain.model.person

import com.example.shikiflow.domain.model.anime.ShikiAnime
import com.example.shikiflow.domain.model.anime.ShikiManga
import kotlinx.serialization.Serializable

@Serializable
data class Work(
    val anime: ShikiAnime?,
    val manga: ShikiManga?,
    val role: String
)