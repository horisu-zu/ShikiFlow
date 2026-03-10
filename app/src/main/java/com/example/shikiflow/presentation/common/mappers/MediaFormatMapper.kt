package com.example.shikiflow.presentation.common.mappers

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
}