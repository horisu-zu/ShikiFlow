package com.example.shikiflow.presentation.common.mappers

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.em
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.formatValue
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.smileyIcon
import com.example.shikiflow.utils.toIcon

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
        score: Float? = null,
        scoreFormat: ScoreFormat,
        mediaType: MediaType = MediaType.ANIME,
        style: TextStyle
    ): Pair<AnnotatedString, Map<String, InlineTextContent>> {
        val inlineContent = mutableMapOf<String, InlineTextContent>()

        val formattedScore = remember(score) {
            score?.let {
                scoreFormat.formatValue(score)
            }
        }

        val progress = if (allEpisodes != 0) {
            buildString {
                append(" ∙ ")
                append(watchedEpisodes ?: 0)
                append("/")
                append(allEpisodes)
            }
        } else ""

        val scoreText = if (formattedScore != null && formattedScore > 0) {
            if (scoreFormat == ScoreFormat.POINT_3) {
                inlineContent["score_icon"] = InlineTextContent(
                    placeholder = Placeholder(
                        width = 1.75.em,
                        height = 1.75.em,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                     smileyIcon(formattedScore).toIcon(
                         tint = style.color
                     )
                }

                buildAnnotatedString {
                    append(" ")
                    appendInlineContent("score_icon")
                }
            } else {
                buildAnnotatedString {
                    append(" ∙ ")
                    append(scoreFormat.displayValue(formattedScore))
                    append("★")
                }
            }
        } else AnnotatedString("")

        val text = buildAnnotatedString {
            when (status) {
                UserRateStatus.WATCHING -> {
                    val resId = if (mediaType == MediaType.ANIME) R.string.media_user_status_anime_watching
                        else R.string.media_user_status_manga_reading

                    append(stringResource(resId))
                    append(progress)
                }
                UserRateStatus.PLANNED ->
                    append(stringResource(R.string.media_user_status_planned))
                UserRateStatus.COMPLETED -> {
                    append(stringResource(R.string.media_user_status_completed))
                    append(scoreText)
                }
                UserRateStatus.REWATCHING -> {
                    val resId = if (mediaType == MediaType.ANIME) R.string.media_user_status_anime_rewatching
                        else R.string.media_user_status_manga_rereading

                    append(stringResource(resId))
                }
                UserRateStatus.PAUSED -> {
                    append(stringResource(R.string.media_user_status_paused))
                    append(progress)
                }
                UserRateStatus.DROPPED -> {
                    append(stringResource(R.string.media_user_status_dropped))
                    if (scoreText.isNotEmpty()) append(scoreText) else append(progress)
                }
                UserRateStatus.UNKNOWN ->
                    append(stringResource(R.string.media_user_status_unknown))
            }
        }

        return text to inlineContent
    }
}