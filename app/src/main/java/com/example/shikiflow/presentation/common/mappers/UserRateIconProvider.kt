package com.example.shikiflow.presentation.common.mappers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.IconResource

object UserRateIconProvider {
    fun UserRateStatus?.icon(mediaType: MediaType): IconResource {
        return when(this) {
            UserRateStatus.WATCHING -> {
                when(mediaType) {
                    MediaType.ANIME -> IconResource.Vector(Icons.Outlined.PlayArrow)
                    MediaType.MANGA -> IconResource.Drawable(R.drawable.ic_manga)
                }
            }
            UserRateStatus.PLANNED -> IconResource.Vector(Icons.Outlined.DateRange)
            UserRateStatus.COMPLETED -> IconResource.Drawable(R.drawable.ic_completed)
            UserRateStatus.REWATCHING -> IconResource.Vector(Icons.Outlined.Refresh)
            UserRateStatus.PAUSED -> IconResource.Drawable(R.drawable.ic_pause)
            UserRateStatus.DROPPED -> IconResource.Vector(Icons.Outlined.Clear)
            else -> IconResource.Drawable(R.drawable.ic_bookmark)
        }
    }

    fun getScoreRatioIcon(scoreRatio: Float): IconResource {
        return when {
            scoreRatio < 1 / 3f -> IconResource.Drawable(resId = R.drawable.ic_thumb_down)
            scoreRatio < 2 / 3f -> IconResource.Drawable(resId = R.drawable.ic_thumbs_up_down)
            else -> IconResource.Drawable(resId = R.drawable.ic_thumb_up)
        }
    }
}