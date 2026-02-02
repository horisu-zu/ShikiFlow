package com.example.shikiflow.domain.model.track

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType

enum class MediaFormat(
    val mediaType: MediaType? = null,
    val displayValue: Int,
    val supportedBy: Set<AuthType> = setOf(AuthType.SHIKIMORI, AuthType.ANILIST)
) {
    TV(MediaType.ANIME,R.string.anime_kind_tv),
    TV_SHORT(MediaType.ANIME, R.string.anime_kind_tv_short),
    MOVIE(MediaType.ANIME, R.string.anime_kind_movie),
    SPECIAL(MediaType.ANIME, R.string.anime_kind_special),
    TV_SPECIAL(MediaType.ANIME, R.string.anime_kind_tv_special, setOf(AuthType.SHIKIMORI)),
    OVA(MediaType.ANIME, R.string.anime_kind_ova),
    ONA(MediaType.ANIME, R.string.anime_kind_ona),
    MUSIC(MediaType.ANIME, R.string.anime_kind_music),
    PV(MediaType.ANIME, R.string.anime_kind_pv, setOf(AuthType.SHIKIMORI)),
    CM(MediaType.ANIME, R.string.anime_kind_cm, setOf(AuthType.SHIKIMORI)),

    MANGA(MediaType.MANGA, R.string.manga_kind_manga),
    MANHWA(MediaType.MANGA, R.string.manga_kind_manhwa, setOf(AuthType.SHIKIMORI)),
    MANHUA(MediaType.MANGA, R.string.manga_kind_manhua, setOf(AuthType.SHIKIMORI)),
    LIGHT_NOVEL(MediaType.MANGA, R.string.manga_kind_light_novel),
    NOVEL(MediaType.MANGA, R.string.manga_kind_novel, setOf(AuthType.SHIKIMORI)),
    ONE_SHOT(MediaType.MANGA, R.string.manga_kind_one_shot),
    DOUJIN(MediaType.MANGA, R.string.manga_kind_doujin, setOf(AuthType.SHIKIMORI)),

    UNKNOWN(null, R.string.common_unknown, emptySet());
}