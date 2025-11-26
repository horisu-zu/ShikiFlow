package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import kotlinx.serialization.Serializable

@Serializable
data class PlayerNavigate(
    val title: String,
    val link: String,
    val translationGroup: String,
    val serialNum: Int,
    val episodesCount: Int
)
