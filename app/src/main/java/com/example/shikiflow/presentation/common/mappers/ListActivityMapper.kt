package com.example.shikiflow.presentation.common.mappers

import android.content.res.Resources
import android.icu.text.ListFormatter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

object ListActivityMapper {
    fun String.withStyledDigits(style: SpanStyle): AnnotatedString = buildAnnotatedString {
        append(this@withStyledDigits)
        Regex("\\d+").findAll(this@withStyledDigits).forEach { match ->
            addStyle(style, match.range.first, match.range.last + 1)
        }
    }

    fun listActivityDescription(
        resources: Resources,
        mediaType: MediaType,
        status: UserRateStatus,
        progress: List<Int>,
        progressVolumes: List<Int>,
        scoreChange: Pair<Int, Int?>?
    ): String {
        val progressText = if(progress.size > 2) {
            ListFormatter.getInstance().format(progress)
        } else progress.joinToString("-")

        val count = progress.size

        return when {
            scoreChange != null -> {
                when {
                    scoreChange.second != null -> resources.getString(R.string.activity_changed_score, scoreChange.first, scoreChange.second!!)
                    status != UserRateStatus.COMPLETED -> resources.getString(R.string.activity_rated, scoreChange.first)
                    else -> resources.getString(R.string.activity_completed_with_score, scoreChange.first)
                }
            }
            progressVolumes.isNotEmpty() -> resources.getQuantityString(
                R.plurals.activity_reading_volumes,
                progressVolumes.size,
                if(progressVolumes.size > 2) {
                    ListFormatter.getInstance().format(progressVolumes)
                } else progressVolumes.joinToString("-")
            )
            progress.isEmpty() -> {
                when(status) {
                    UserRateStatus.WATCHING -> {
                        when(mediaType) {
                            MediaType.ANIME -> resources.getString(R.string.activity_watching)
                            MediaType.MANGA -> resources.getString(R.string.activity_reading)
                        }
                    }
                    UserRateStatus.PLANNED -> {
                        when(mediaType) {
                            MediaType.ANIME -> resources.getString(R.string.activity_planned_watch)
                            MediaType.MANGA -> resources.getString(R.string.activity_planned_read)
                        }
                    }
                    UserRateStatus.COMPLETED -> resources.getString(R.string.activity_completed)
                    UserRateStatus.REWATCHING -> {
                        when(mediaType) {
                            MediaType.ANIME -> resources.getString(R.string.activity_rewatching)
                            MediaType.MANGA -> resources.getString(R.string.activity_rereading)
                        }
                    }
                    UserRateStatus.PAUSED -> {
                        when(mediaType) {
                            MediaType.ANIME -> resources.getString(R.string.activity_paused_watching)
                            MediaType.MANGA -> resources.getString(R.string.activity_paused_reading)
                        }
                    }
                    UserRateStatus.DROPPED -> resources.getString(R.string.media_user_status_dropped)
                    UserRateStatus.UNKNOWN -> resources.getString(R.string.common_unknown)
                }
            }
            else -> {
                when(status) {
                    UserRateStatus.WATCHING -> {
                        when(mediaType) {
                            MediaType.ANIME -> resources.getQuantityString(R.plurals.activity_watching_with_progress, count, progressText)
                            MediaType.MANGA -> resources.getQuantityString(R.plurals.activity_reading_with_progress, count, progressText)
                        }
                    }
                    UserRateStatus.PAUSED -> {
                        when(mediaType) {
                            MediaType.ANIME -> resources.getQuantityString(R.plurals.activity_paused_watching_with_progress, count, progressText)
                            MediaType.MANGA -> resources.getQuantityString(R.plurals.activity_paused_reading_with_progress, count, progressText)
                        }
                    }
                    UserRateStatus.DROPPED -> {
                        when(mediaType) {
                            MediaType.ANIME -> resources.getQuantityString(R.plurals.activity_dropped_watching, count, progressText)
                            MediaType.MANGA -> resources.getQuantityString(R.plurals.activity_dropped_reading, count, progressText)
                        }
                    }
                    else -> resources.getString(R.string.common_unknown)
                }
            }
        }
    }
}