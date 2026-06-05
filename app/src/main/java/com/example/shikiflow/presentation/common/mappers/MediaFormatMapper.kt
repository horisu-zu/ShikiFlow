package com.example.shikiflow.presentation.common.mappers

import androidx.compose.ui.graphics.Color
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.MediaFormat

object MediaFormatMapper {
    fun MediaFormat.displayValue(): Int {
        return when(this) {
            MediaFormat.TV -> R.string.anime_format_tv
            MediaFormat.TV_SHORT -> R.string.anime_format_tv_short
            MediaFormat.MOVIE -> R.string.anime_format_movie
            MediaFormat.SPECIAL -> R.string.anime_format_special
            MediaFormat.TV_SPECIAL -> R.string.anime_format_tv_special
            MediaFormat.OVA -> R.string.anime_format_ova
            MediaFormat.ONA -> R.string.anime_format_ona
            MediaFormat.MUSIC -> R.string.anime_format_music
            MediaFormat.PV -> R.string.anime_format_pv
            MediaFormat.CM -> R.string.anime_format_cm
            MediaFormat.MANGA -> R.string.manga_format_manga
            MediaFormat.MANHWA -> R.string.manga_format_manhwa
            MediaFormat.MANHUA -> R.string.manga_format_manhua
            MediaFormat.LIGHT_NOVEL -> R.string.manga_format_light_novel
            MediaFormat.NOVEL -> R.string.manga_format_novel
            MediaFormat.ONE_SHOT -> R.string.manga_format_one_shot
            MediaFormat.DOUJIN -> R.string.manga_format_doujin
            MediaFormat.UNKNOWN -> R.string.common_unknown
        }
    }

    fun MediaFormat.color(): Color {
        return when(this) {
            MediaFormat.TV, MediaFormat.MANGA -> Color(0xFFAE62CF)
            MediaFormat.TV_SHORT -> Color(0xFF62CFCF)
            MediaFormat.MOVIE, MediaFormat.ONE_SHOT -> Color(0xFFD4C862)
            MediaFormat.SPECIAL, MediaFormat.LIGHT_NOVEL -> Color(0xFF62CF71)
            MediaFormat.OVA -> Color(0xFF628ACF)
            MediaFormat.ONA -> Color(0xFFCF6562)
            MediaFormat.MUSIC -> Color(0xFFD51C5B)
            else -> Color(0xFF8C8C8C)
        }
    }

    fun MediaFormat.isManga(): Boolean {
        return this in setOf(MediaFormat.MANGA, MediaFormat.MANHWA,
            MediaFormat.MANHUA, MediaFormat.ONE_SHOT, MediaFormat.DOUJIN)
    }
}