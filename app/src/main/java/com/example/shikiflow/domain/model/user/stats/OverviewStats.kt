package com.example.shikiflow.domain.model.user.stats

import com.example.shikiflow.domain.model.media_details.CountryOfOrigin
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

data class OverviewStats(
    val shortStats: List<ShortOverviewStat> = emptyList(),
    val scoreStatsTitles: List<Stat<Int>> = emptyList(),
    val scoreStatsTime: List<Stat<Int>> = emptyList(),
    val statusesStats: List<Stat<UserRateStatus>> = emptyList(),
    val lengthStatsTitles: List<Stat<String>> = emptyList(),
    val lengthStatsTime: List<Stat<String>> = emptyList(),
    val lengthStatsScore: List<Stat<String>> = emptyList(),
    val formatStats: List<Stat<MediaFormat>> = emptyList(),
    val countryStats: List<Stat<CountryOfOrigin>> = emptyList(),
    val releaseYearStatsTitles: List<Stat<Int>> = emptyList(),
    val releaseYearStatsTime: List<Stat<Int>> = emptyList(),
    val releaseYearStatsScore: List<Stat<Int>> = emptyList(),
    val startYearStatsTitles: List<Stat<Int>> = emptyList(),
    val startYearStatsTime: List<Stat<Int>> = emptyList(),
    val startYearStatsScore: List<Stat<Int>> = emptyList()
)

data class MediaTypeStats<T> (
    val animeStats: T? = null,
    val mangaStats: T? = null
) {
    operator fun get(mediaType: MediaType): T? = when (mediaType) {
        MediaType.ANIME -> animeStats
        MediaType.MANGA -> mangaStats
    }
}
