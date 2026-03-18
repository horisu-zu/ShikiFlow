package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

data class OverviewStats(
    val shortStats: List<ShortOverviewStat> = emptyList(),
    val scoreStatsTitles: List<Stat<Int>> = emptyList(),
    val scoreStatsTime: List<Stat<Int>> = emptyList(),
    val statusesStats: List<Stat<UserRateStatus>> = emptyList(),
    val lengthStatsTitles: List<Stat<String>> = emptyList(),
    val lengthStatsTime: List<Stat<String>> = emptyList(),
    val lengthStatsScore: List<Stat<String>> = emptyList()
)

data class MediaTypeStats<T> (
    val animeStats: T? = null,
    val mangaStats: T? = null
) {
    operator fun get(mediaType: MediaType): T? = when (mediaType) {
        MediaType.ANIME -> animeStats
        MediaType.MANGA -> mangaStats
    }

    fun getMediaTypes(): List<MediaType> {
        return buildList {
            if(animeStats != null) add(MediaType.ANIME)
            if(mangaStats != null) add(MediaType.MANGA)
        }
    }

    fun isEmpty(): Boolean {
        return animeStats == null && mangaStats == null
    }
}
