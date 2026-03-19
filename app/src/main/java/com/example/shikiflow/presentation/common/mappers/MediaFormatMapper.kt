package com.example.shikiflow.presentation.common.mappers

import androidx.compose.ui.graphics.Color
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.MediaFormat

object MediaFormatMapper {
    fun MediaFormat.displayValue(): Int {
        return when(this) {
            MediaFormat.TV -> R.string.anime_kind_tv
            MediaFormat.TV_SHORT -> R.string.anime_kind_tv_short
            MediaFormat.MOVIE -> R.string.anime_kind_movie
            MediaFormat.SPECIAL -> R.string.anime_kind_special
            MediaFormat.TV_SPECIAL -> R.string.anime_kind_tv_special
            MediaFormat.OVA -> R.string.anime_kind_ova
            MediaFormat.ONA -> R.string.anime_kind_ona
            MediaFormat.MUSIC -> R.string.anime_kind_music
            MediaFormat.PV -> R.string.anime_kind_pv
            MediaFormat.CM -> R.string.anime_kind_cm
            MediaFormat.MANGA -> R.string.manga_kind_manga
            MediaFormat.MANHWA -> R.string.manga_kind_manhwa
            MediaFormat.MANHUA -> R.string.manga_kind_manhua
            MediaFormat.LIGHT_NOVEL -> R.string.manga_kind_light_novel
            MediaFormat.NOVEL -> R.string.manga_kind_novel
            MediaFormat.ONE_SHOT -> R.string.manga_kind_one_shot
            MediaFormat.DOUJIN -> R.string.manga_kind_doujin
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
}