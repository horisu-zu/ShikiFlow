package com.example.shikiflow.data.datasource.dto

import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ShikiTargetType {
    @SerialName("Anime") ANIME,
    @SerialName("Manga") MANGA;

    companion object {
        fun MediaType.toShikiType(): ShikiTargetType {
            return when(this) {
                MediaType.ANIME -> ANIME
                MediaType.MANGA -> MANGA
            }
        }

        fun ShikiTargetType.toMediaType(): MediaType {
            return when(this) {
                ANIME -> MediaType.ANIME
                MANGA -> MediaType.MANGA
            }
        }
    }
}