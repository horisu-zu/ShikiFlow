package com.example.shikiflow.presentation.common.mappers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

object UserRateStatusMapper {
    fun UserRateStatus.mapStatus(mediaType: MediaType = MediaType.ANIME): Int {
        return when(this) {
            UserRateStatus.WATCHING -> if(mediaType == MediaType.ANIME) {
                R.string.media_user_status_anime_watching
            } else { R.string.media_user_status_manga_reading }
            UserRateStatus.PLANNED -> R.string.media_user_status_planned
            UserRateStatus.COMPLETED -> R.string.media_user_status_completed
            UserRateStatus.REWATCHING -> if(mediaType == MediaType.ANIME) {
                R.string.media_user_status_anime_rewatching
            } else { R.string.media_user_status_manga_rereading }
            UserRateStatus.PAUSED -> R.string.media_user_status_paused
            UserRateStatus.DROPPED -> R.string.media_user_status_dropped
            UserRateStatus.UNKNOWN -> R.string.media_user_status_unknown
        }
    }

    @Composable
    fun mapUserRateStatusToString(
        status: UserRateStatus,
        watchedEpisodes: Int?,
        allEpisodes: Int,
        score: Int? = null,
        mediaType: MediaType = MediaType.ANIME
    ): String {
        val progressSuffix = if (allEpisodes != 0) {
            stringResource(R.string.progress_suffix, watchedEpisodes ?: 0, allEpisodes)
        } else ""

        val scoreSuffix = if (score != null && score > 0) {
            stringResource(R.string.score_suffix, score.toString())
        } else ""

        return when (status) {
            UserRateStatus.WATCHING -> {
                val resId = if (mediaType == MediaType.ANIME) R.string.media_user_status_anime_watching
                else R.string.media_user_status_manga_reading
                stringResource(resId) + progressSuffix
            }
            UserRateStatus.PLANNED -> stringResource(R.string.media_user_status_planned)
            UserRateStatus.COMPLETED -> {
                stringResource(R.string.media_user_status_completed) + scoreSuffix
            }
            UserRateStatus.REWATCHING -> {
                val resId = if (mediaType == MediaType.ANIME) R.string.media_user_status_anime_rewatching
                else R.string.media_user_status_manga_rereading
                stringResource(resId)
            }
            UserRateStatus.PAUSED -> {
                stringResource(R.string.media_user_status_paused) + progressSuffix
            }
            UserRateStatus.DROPPED -> {
                stringResource(R.string.media_user_status_dropped) + (scoreSuffix.takeIf { it.isNotEmpty() } ?: progressSuffix)
            }
            UserRateStatus.UNKNOWN -> stringResource(R.string.media_user_status_unknown)
        }
    }
}