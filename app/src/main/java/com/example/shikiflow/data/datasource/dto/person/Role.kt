package com.example.shikiflow.data.datasource.dto.person

import com.example.shikiflow.data.datasource.dto.ShikiAnime
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val animes: List<ShikiAnime>,
    val characters: List<Character>
)