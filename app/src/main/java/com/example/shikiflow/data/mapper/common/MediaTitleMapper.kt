package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.fragment.MediaTitle
import com.example.graphql.shikimori.AnimeDetailsQuery
import com.example.graphql.shikimori.MangaDetailsQuery
import com.example.shikiflow.domain.model.media_details.MediaTitle as DomainTitle

object MediaTitleMapper {
    fun MediaTitle?.toDomainTitle(): DomainTitle {
        return DomainTitle(
            romaji = this?.romaji ?: "",
            english = this?.english,
            russian = null,
            native = this?.native
        )
    }

    fun AnimeDetailsQuery.Anime.toDomainTitle(): DomainTitle {
        return DomainTitle(
            romaji = name,
            english = english,
            russian = russian,
            native = japanese
        )
    }

    fun MangaDetailsQuery.Manga.toDomainTitle(): DomainTitle {
        return DomainTitle(
            romaji = name,
            english = english,
            russian = russian,
            native = japanese
        )
    }

    fun String.toDomainTitle(
        english: String?,
        russian: String?,
        native: String?
    ): DomainTitle {
        return DomainTitle(
            romaji = this,
            english = english,
            russian = russian,
            native = native
        )
    }
}