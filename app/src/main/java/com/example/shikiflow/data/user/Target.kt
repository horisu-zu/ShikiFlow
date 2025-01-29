package com.example.shikiflow.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Target(
    val id: Long,
    val name: String,
    val russian: String,
    val image: ImageUrls,
    val url: String,
    val kind: String,
    val score: String,
    val status: String,
    val episodes: Int = 0,
    val episodes_aired: Int = 0,
    val aired_on: String?,
    val released_on: String?
)

@Serializable
enum class TargetType {
    @SerialName("Anime") ANIME,
    @SerialName("Manga") MANGA
}