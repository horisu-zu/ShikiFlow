package com.example.shikiflow.presentation.common.mappers

import android.icu.text.ListFormatter
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.ListActivity

object ListActivityMapper {
    fun String.withStyledDigits(style: SpanStyle): AnnotatedString = buildAnnotatedString {
        append(this@withStyledDigits)
        Regex("\\d+").findAll(this@withStyledDigits).forEach { match ->
            addStyle(style, match.range.first, match.range.last + 1)
        }
    }

    @Composable
    fun ListActivity.description(): String {
        val progressText = if(progress.size > 2) {
            ListFormatter.getInstance().format(progress)
        } else progress.joinToString("-")

        val count = progress.size

        return when {
            scoreChange != null -> {
                when {
                    scoreChange.second != null -> stringResource(R.string.activity_changed_score, scoreChange.first, scoreChange.second!!)
                    status != UserRateStatus.COMPLETED -> stringResource(R.string.activity_rated, scoreChange.first)
                    else -> stringResource(R.string.activity_completed_with_score, scoreChange.first)
                }
            }
            progressVolumes.isNotEmpty() -> pluralStringResource(
                id = R.plurals.activity_reading_volumes,
                count = progressVolumes.size,
                if(progressVolumes.size > 2) {
                    ListFormatter.getInstance().format(progressVolumes)
                } else progressVolumes.joinToString("-")
            )
            progress.isEmpty() -> {
                when(status) {
                    UserRateStatus.WATCHING -> {
                        when(mediaType) {
                            MediaType.ANIME -> stringResource(R.string.activity_watching)
                            MediaType.MANGA -> stringResource(R.string.activity_reading)
                            null -> stringResource(R.string.common_unknown)
                        }
                    }
                    UserRateStatus.PLANNED -> stringResource(R.string.activity_planned)
                    UserRateStatus.COMPLETED -> stringResource(R.string.activity_completed)
                    UserRateStatus.REWATCHING -> {
                        when(mediaType) {
                            MediaType.ANIME -> stringResource(R.string.activity_rewatching)
                            MediaType.MANGA -> stringResource(R.string.activity_rereading)
                            null -> stringResource(R.string.common_unknown)
                        }
                    }
                    UserRateStatus.PAUSED -> {
                        when(mediaType) {
                            MediaType.ANIME -> stringResource(R.string.activity_paused_watching)
                            MediaType.MANGA -> stringResource(R.string.activity_paused_reading)
                            null -> stringResource(R.string.common_unknown)
                        }
                    }
                    UserRateStatus.DROPPED -> stringResource(R.string.media_user_status_dropped)
                    UserRateStatus.UNKNOWN -> stringResource(R.string.common_unknown)
                }
            }
            else -> {
                when(status) {
                    UserRateStatus.WATCHING -> {
                        when(mediaType) {
                            MediaType.ANIME -> pluralStringResource(R.plurals.activity_watching_with_progress, count, progressText)
                            MediaType.MANGA -> pluralStringResource(R.plurals.activity_reading_with_progress, count, progressText)
                            null -> stringResource(R.string.common_unknown)
                        }
                    }
                    UserRateStatus.PLANNED -> stringResource(R.string.activity_planned)
                    UserRateStatus.PAUSED -> {
                        when(mediaType) {
                            MediaType.ANIME -> pluralStringResource(R.plurals.activity_paused_watching_with_progress, count, progressText)
                            MediaType.MANGA -> pluralStringResource(R.plurals.activity_paused_reading_with_progress, count, progressText)
                            null -> stringResource(R.string.common_unknown)
                        }
                    }
                    UserRateStatus.DROPPED -> {
                        when(mediaType) {
                            MediaType.ANIME -> pluralStringResource(R.plurals.activity_dropped_watching, count, progressText)
                            MediaType.MANGA -> pluralStringResource(R.plurals.activity_dropped_reading, count, progressText)
                            null -> stringResource(R.string.common_unknown)
                        }
                    }
                    else -> stringResource(R.string.common_unknown)
                }
            }
        }
    }
}