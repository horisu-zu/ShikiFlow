package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeMetadata(
    val link: String,
    val translationGroup: String,
    val episodeNum: Int,
    val firstEpisode: Int,
    val lastEpisode: Int
)
