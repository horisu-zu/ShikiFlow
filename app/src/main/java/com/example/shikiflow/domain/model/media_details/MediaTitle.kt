package com.example.shikiflow.domain.model.media_details

import kotlinx.serialization.Serializable

@Serializable
data class MediaTitle(
    val romaji: String,
    val english: String?,
    val russian: String?,
    val native: String?
) {
    companion object {
        fun MediaTitle.preferred(titleType: PreferredTitleType): String {
            return when(titleType) {
                PreferredTitleType.ROMAJI -> romaji
                PreferredTitleType.ENGLISH -> english ?: romaji
                PreferredTitleType.RUSSIAN -> russian ?: romaji
                PreferredTitleType.NATIVE -> native ?: romaji
            }
        }
    }
}

enum class PreferredTitleType {
    ROMAJI,
    ENGLISH,
    RUSSIAN,
    NATIVE
}