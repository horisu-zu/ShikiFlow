package com.example.shikiflow.domain.model.track

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType

enum class MediaFormat(
    val mediaType: MediaType? = null,
    val supportedBy: Set<AuthType> = setOf(AuthType.SHIKIMORI, AuthType.ANILIST)
) {
    TV(MediaType.ANIME),
    TV_SHORT(MediaType.ANIME),
    MOVIE(MediaType.ANIME),
    SPECIAL(MediaType.ANIME),
    TV_SPECIAL(MediaType.ANIME, setOf(AuthType.SHIKIMORI)),
    OVA(MediaType.ANIME),
    ONA(MediaType.ANIME),
    MUSIC(MediaType.ANIME),
    PV(MediaType.ANIME, setOf(AuthType.SHIKIMORI)),
    CM(MediaType.ANIME, setOf(AuthType.SHIKIMORI)),

    MANGA(MediaType.MANGA),
    MANHWA(MediaType.MANGA, setOf(AuthType.SHIKIMORI)),
    MANHUA(MediaType.MANGA, setOf(AuthType.SHIKIMORI)),
    LIGHT_NOVEL(MediaType.MANGA),
    NOVEL(MediaType.MANGA, setOf(AuthType.SHIKIMORI)),
    ONE_SHOT(MediaType.MANGA),
    DOUJIN(MediaType.MANGA, setOf(AuthType.SHIKIMORI)),

    UNKNOWN(null, emptySet());
}