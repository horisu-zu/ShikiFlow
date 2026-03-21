package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.IconResource

enum class StatType {
    COUNT,
    MEAN_SCORE,
    TIME;

    companion object {
        fun StatType.displayValue(mediaType: MediaType): Int {
            return when(this) {
                COUNT -> R.string.stat_type_count
                MEAN_SCORE -> R.string.overview_stat_mean_score
                TIME -> when(mediaType) {
                    MediaType.ANIME -> R.string.genre_stats_time_anime
                    MediaType.MANGA -> R.string.overview_stat_chapters_manga
                }
            }
        }

        fun StatType.iconResource(mediaType: MediaType): IconResource {
            return when(this) {
                COUNT -> when(mediaType) {
                    MediaType.ANIME -> IconResource.Drawable(resId = R.drawable.ic_anime)
                    MediaType.MANGA -> IconResource.Drawable(resId = R.drawable.ic_manga)
                }
                MEAN_SCORE -> IconResource.Drawable(resId = R.drawable.ic_percentage)
                TIME -> when(mediaType) {
                    MediaType.ANIME -> IconResource.Vector(imageVector = Icons.Default.DateRange)
                    MediaType.MANGA -> IconResource.Drawable(resId = R.drawable.ic_unselected_book)
                }
            }
        }
    }
}