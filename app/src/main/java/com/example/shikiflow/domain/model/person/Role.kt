package com.example.shikiflow.domain.model.person

import com.example.shikiflow.domain.model.anime.ShikiAnime
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val animes: List<ShikiAnime>,
    val characters: List<Character>
)