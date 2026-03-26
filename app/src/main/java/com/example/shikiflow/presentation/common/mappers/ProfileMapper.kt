package com.example.shikiflow.presentation.common.mappers

import android.content.res.Resources
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.stats.CombinedStat
import com.example.shikiflow.domain.model.user.stats.OverviewStatType
import com.example.shikiflow.presentation.common.TabRowItem
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.UserStatsSectionType
import com.example.shikiflow.utils.IconResource

object ProfileMapper {
    fun FavoriteCategory.displayValue(): Int {
        return when(this) {
            FavoriteCategory.ANIME -> R.string.media_type_anime
            FavoriteCategory.MANGA -> R.string.media_type_manga
            FavoriteCategory.CHARACTER -> R.string.details_characters
            FavoriteCategory.STAFF -> R.string.favorite_staff
            FavoriteCategory.STUDIO -> R.string.favorite_studios
            FavoriteCategory.SEYU -> R.string.shiki_person_type_seyu
            FavoriteCategory.MANGAKA -> R.string.shiki_person_type_mangaka
            FavoriteCategory.PRODUCER -> R.string.shiki_person_type_producer
            FavoriteCategory.OTHER_PERSON -> R.string.shiki_person_type_other
        }
    }

    fun FavoriteCategory.iconResource(): IconResource {
        return when(this) {
            FavoriteCategory.ANIME -> IconResource.Drawable(R.drawable.ic_anime)
            FavoriteCategory.MANGA -> IconResource.Drawable(R.drawable.ic_manga)
            FavoriteCategory.CHARACTER -> IconResource.Drawable(R.drawable.ic_character)
            FavoriteCategory.STAFF -> IconResource.Vector(Icons.Default.Person)
            FavoriteCategory.STUDIO -> IconResource.Drawable(R.drawable.ic_film_camera)
            FavoriteCategory.SEYU -> IconResource.Drawable(R.drawable.ic_microphone)
            FavoriteCategory.MANGAKA -> IconResource.Drawable(R.drawable.ic_drawing)
            FavoriteCategory.PRODUCER -> IconResource.Drawable(R.drawable.ic_film_camera)
            FavoriteCategory.OTHER_PERSON -> IconResource.Vector(Icons.Default.Person)
        }
    }

    fun FavoriteCategory.toTabRowItem(): TabRowItem<FavoriteCategory> {
        return TabRowItem(
            value = this,
            titleRes = this.displayValue(),
            iconResource = this.iconResource()
        )
    }

    fun UserStatsSectionType.displayValue(): Int {
        return when(this) {
            UserStatsSectionType.OVERVIEW -> R.string.user_stats_section_overview
            UserStatsSectionType.GENRES -> R.string.user_stats_section_genres
            UserStatsSectionType.TAGS -> R.string.user_stats_section_tags
            UserStatsSectionType.VOICE_ACTORS -> R.string.user_stats_section_voice_actors
            UserStatsSectionType.STUDIOS -> R.string.favorite_studios
            UserStatsSectionType.STAFF -> R.string.staff_title
        }
    }

    fun StatsBarType.displayValue(mediaType: MediaType): Int {
        return when(this) {
            StatsBarType.TITLES -> when(mediaType) {
                MediaType.ANIME -> R.string.stats_bar_type_titles_watched
                MediaType.MANGA -> R.string.stats_bar_type_titles_read
            }
            StatsBarType.TIME -> when(mediaType) {
                MediaType.ANIME -> R.string.stats_bar_type_hours_watched
                MediaType.MANGA -> R.string.stats_bar_type_chapters_read
            }
            StatsBarType.MEAN_SCORE -> R.string.stats_bar_type_mean_score
        }
    }

    fun OverviewStatType.displayValue(mediaType: MediaType) : Int {
        return when(this) {
            OverviewStatType.TITLE -> when(mediaType) {
                MediaType.ANIME -> R.string.overview_stat_title_anime
                MediaType.MANGA -> R.string.overview_stat_title_manga
            }
            OverviewStatType.EPISODE -> when(mediaType) {
                MediaType.ANIME -> R.string.overview_stat_episodes_anime
                MediaType.MANGA -> R.string.overview_stat_chapters_manga
            }
            OverviewStatType.TIME -> when(mediaType) {
                MediaType.ANIME -> R.string.overview_stat_time_anime
                MediaType.MANGA -> R.string.overview_stat_volumes_manga
            }
            OverviewStatType.PLANNED -> when(mediaType) {
                MediaType.ANIME -> R.string.overview_stat_planned_anime
                MediaType.MANGA -> R.string.overview_stat_planned_manga
            }
            OverviewStatType.MEAN_SCORE -> R.string.overview_stat_mean_score
            OverviewStatType.STANDARD_DEVIATION -> R.string.overview_stat_standard_deviation
        }
    }

    fun OverviewStatType.iconResource(mediaType: MediaType) : IconResource {
        return when(this) {
            OverviewStatType.TITLE -> when(mediaType) {
                MediaType.ANIME -> IconResource.Drawable(resId = R.drawable.ic_anime)
                MediaType.MANGA -> IconResource.Drawable(resId = R.drawable.ic_manga)
            }
            OverviewStatType.EPISODE -> when(mediaType) {
                MediaType.ANIME -> IconResource.Vector(imageVector = Icons.Default.PlayArrow)
                MediaType.MANGA -> IconResource.Drawable(resId = R.drawable.ic_bookmark_outlined)
            }
            OverviewStatType.TIME -> when(mediaType) {
                MediaType.ANIME -> IconResource.Vector(imageVector = Icons.Default.DateRange)
                MediaType.MANGA -> IconResource.Drawable(resId = R.drawable.ic_unselected_book)
            }
            OverviewStatType.PLANNED -> IconResource.Drawable(resId = R.drawable.ic_sand_clock)
            OverviewStatType.MEAN_SCORE -> IconResource.Drawable(resId = R.drawable.ic_percentage)
            OverviewStatType.STANDARD_DEVIATION -> IconResource.Drawable(resId = R.drawable.ic_division)
        }
    }

    fun <T: CombinedStat> List<T>.sortedBy(
        type: StatsBarType,
        mediaType: MediaType
    ) = when (type) {
        StatsBarType.TITLES -> sortedByDescending { it.count }

        StatsBarType.TIME ->
            if (mediaType == MediaType.ANIME)
                sortedByDescending { it.timeWatched }
            else
                sortedByDescending { it.chaptersRead }

        StatsBarType.MEAN_SCORE -> sortedByDescending { it.meanScore }
    }

    fun Resources.formatDaysHours(value: Float): String {
        val days = value.toInt()
        val hours = ((value - days) * 24).toInt()

        return getString(R.string.days_hours, days, hours)
    }

    fun SocialCategory.displayValue(): Int {
        return when(this) {
            SocialCategory.FOLLOWINGS -> R.string.social_category_followings
            SocialCategory.FOLLOWERS -> R.string.social_category_followers
            SocialCategory.THREADS -> R.string.social_category_threads
            SocialCategory.COMMENTS -> R.string.social_category_thread_comments
        }
    }
}