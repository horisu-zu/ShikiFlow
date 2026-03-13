package com.example.shikiflow.presentation.common.mappers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.settings.BrowseUiMode
import com.example.shikiflow.domain.model.settings.ChapterUIMode
import com.example.shikiflow.domain.model.track.MainTrackMode
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.ThemeMode

object SettingsMapper {
    fun ThemeMode.displayValue(): Int {
        return when(this) {
            ThemeMode.SYSTEM -> R.string.theme_mode_system
            ThemeMode.LIGHT -> R.string.theme_mode_light
            ThemeMode.DARK -> R.string.theme_mode_dark
        }
    }

    fun ThemeMode.iconResource(): IconResource {
        return when(this) {
            ThemeMode.SYSTEM -> IconResource.Drawable(resId = R.drawable.ic_system_theme)
            ThemeMode.LIGHT -> IconResource.Drawable(resId = R.drawable.ic_light_theme)
            ThemeMode.DARK -> IconResource.Drawable(resId = R.drawable.ic_dark_theme)
        }
    }

    fun AppUiMode.displayValue(): Int {
        return when(this) {
            AppUiMode.LIST -> R.string.app_ui_mode_list
            AppUiMode.GRID -> R.string.app_ui_mode_grid
        }
    }

    fun BrowseUiMode.displayValue(): Int {
        return when(this) {
            BrowseUiMode.AUTO -> R.string.browse_ui_mode_auto
            BrowseUiMode.LIST -> R.string.browse_ui_mode_list
            BrowseUiMode.GRID -> R.string.browse_ui_mode_grid
        }
    }

    fun BrowseUiMode.iconResource(): IconResource {
        return when(this) {
            BrowseUiMode.AUTO -> IconResource.Drawable(resId = R.drawable.ic_stars)
            BrowseUiMode.LIST -> IconResource.Vector(imageVector = Icons.AutoMirrored.Filled.List)
            BrowseUiMode.GRID -> IconResource.Drawable(resId = R.drawable.ic_grid)
        }
    }

    fun ChapterUIMode.displayValue(): Int {
        return when(this) {
            ChapterUIMode.PAGE -> R.string.chapter_ui_mode_page
            ChapterUIMode.SCROLL -> R.string.chapter_ui_mode_scroll
        }
    }

    fun ChapterUIMode.iconResource(): IconResource {
        return when(this) {
            ChapterUIMode.PAGE -> IconResource.Drawable(resId = R.drawable.ic_manga)
            ChapterUIMode.SCROLL -> IconResource.Drawable(resId = R.drawable.ic_scroll)
        }
    }

    fun MainTrackMode.displayValue(): Int {
        return when(this) {
            MainTrackMode.ANIME -> R.string.media_type_anime
            MainTrackMode.MANGA -> R.string.media_type_manga
        }
    }
}