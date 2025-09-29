package com.example.shikiflow.domain.model.mapper

import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType

object UserRateStatusConstants {
    private val statusChipsMap = mapOf(
        MediaType.ANIME to listOf(
            R.string.media_user_status_anime_watching,
            R.string.media_user_status_planned,
            R.string.media_user_status_completed,
            R.string.media_user_status_anime_rewatching,
            R.string.media_user_status_on_hold,
            R.string.media_user_status_dropped
        ),
        MediaType.MANGA to listOf(
            R.string.media_user_status_manga_reading,
            R.string.media_user_status_planned,
            R.string.media_user_status_completed,
            R.string.media_user_status_manga_rereading,
            R.string.media_user_status_on_hold,
            R.string.media_user_status_dropped
        )
    )

    fun getStatusChips(contentType: MediaType): List<Int> =
        statusChipsMap[contentType] ?: emptyList()

    fun convertToApiStatus(index: Int): UserRateStatusEnum = when(index) {
        0 -> UserRateStatusEnum.watching
        1 -> UserRateStatusEnum.planned
        2 -> UserRateStatusEnum.completed
        3 -> UserRateStatusEnum.rewatching
        4 -> UserRateStatusEnum.on_hold
        else ->  UserRateStatusEnum.dropped
    }
}