package com.example.shikiflow.presentation.common.mappers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.media_details.MediaSeason
import com.example.shikiflow.utils.IconResource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

object BrowseTypeMapper {
    fun BrowseType.displayValue(): Int {
        return when(this) {
            BrowseType.AnimeBrowseType.ONGOING -> R.string.browse_type_anime_ongoing
            BrowseType.AnimeBrowseType.ANIME_TOP,
            BrowseType.MangaBrowseType.MANGA_TOP -> R.string.browse_type_top_score
            BrowseType.AnimeBrowseType.ANIME_POPULARITY,
            BrowseType.MangaBrowseType.MANGA_POPULARITY -> R.string.browse_type_popular
            BrowseType.AnimeBrowseType.TRENDING_NOW,
            BrowseType.MangaBrowseType.TRENDING_NOW -> R.string.browse_type_trending_now
            BrowseType.AnimeBrowseType.POPULAR_THIS_SEASON -> R.string.browse_type_popular_this_season
            BrowseType.AnimeBrowseType.UPCOMING_NEXT_SEASON -> R.string.browse_type_upcoming_next_season
            BrowseType.MangaBrowseType.POPULAR_MANHWA -> R.string.browse_type_popular_manhwa
            BrowseType.AnimeBrowseType.NEWLY_ADDED,
            BrowseType.MangaBrowseType.NEWLY_ADDED -> R.string.browse_type_newly_added
        }
    }

    fun BrowseType.iconResource(): IconResource {
        return when(this) {
            BrowseType.AnimeBrowseType.ONGOING -> IconResource.Vector(Icons.Default.DateRange)
            BrowseType.AnimeBrowseType.ANIME_TOP -> IconResource.Drawable(R.drawable.ic_anime)
            BrowseType.AnimeBrowseType.ANIME_POPULARITY,
            BrowseType.MangaBrowseType.MANGA_POPULARITY -> IconResource.Drawable(R.drawable.ic_trend_up)
            BrowseType.MangaBrowseType.MANGA_TOP -> IconResource.Drawable(R.drawable.ic_manga)
            else -> IconResource.Vector(Icons.Default.Check)
        }
    }
}