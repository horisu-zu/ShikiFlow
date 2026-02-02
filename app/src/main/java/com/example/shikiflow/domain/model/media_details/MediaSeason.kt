package com.example.shikiflow.domain.model.media_details

data class MediaSeason(
    val season: MediaSeasonEnum,
    val year: Int
)

enum class MediaSeasonEnum {
    WINTER,
    SPRING,
    SUMMER,
    FALL
}
