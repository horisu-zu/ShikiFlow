package com.example.shikiflow.domain.model.media_details

import com.example.shikiflow.domain.model.auth.AuthType

enum class Genre(val supportedBy: Set<AuthType> = setOf(AuthType.ANILIST, AuthType.SHIKIMORI)) {
    ACTION,
    ADVENTURE,
    AVANT_GARDE(supportedBy = setOf(AuthType.SHIKIMORI)),
    COMEDY,
    DRAMA,
    ECCHI,
    EROTICA(supportedBy = setOf(AuthType.SHIKIMORI)),
    FANTASY,
    GOURMET(supportedBy = setOf(AuthType.SHIKIMORI)),
    HENTAI,
    HORROR,
    MAHOU_SHOUJO(supportedBy = setOf(AuthType.ANILIST)),
    MECHA(supportedBy = setOf(AuthType.ANILIST)),
    MUSIC(supportedBy = setOf(AuthType.ANILIST)),
    MYSTERY,
    PSYCHOLOGICAL(supportedBy = setOf(AuthType.ANILIST)),
    ROMANCE,
    SCI_FI,
    SLICE_OF_LIFE,
    SPORTS,
    SUPERNATURAL,
    THRILLER,
    BOYS_LOVE(supportedBy = setOf(AuthType.SHIKIMORI)),
    GIRLS_LOVE(supportedBy = setOf(AuthType.SHIKIMORI)),
    YURI(supportedBy = setOf(AuthType.SHIKIMORI)),
    YAOI(supportedBy = setOf(AuthType.SHIKIMORI))
}