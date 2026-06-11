package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.MediaExternalLinksQuery
import com.example.shikiflow.data.datasource.dto.ExternalLink
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import kotlin.text.split

object ExternalLinksMapper {
    fun ExternalLink.toDomain(): ExternalLinkData {
        return ExternalLinkData(
            icon = null,
            siteName = mapShikimoriSiteName(kind),
            url = url
        )
    }

    private fun mapShikimoriSiteName(siteName: String): String {
        return when(siteName) {
            "myanimelist" -> "MyAnimeList"
            "anime_db" -> "AniDB"
            "kinopoisk" -> "KinoPoisk"
            "smotret_anime" -> "Anime 365"
            "shiki" -> "Shikimori"
            "kinopoisk_hd" -> "KinoPoisk HD"
            "remanga" -> "ReManga"
            "mangalib" -> "MangaLib"
            "mangaupdates" -> "Baka-Updates"
            "readmanga" -> "ReadManga"
            "mangadex" -> "MangaDex"
            "mangafox" -> "MangaFox"
            "novel_tl" -> "Novel.tl"
            "ruranobe" -> "RuRanobe"
            "ranobelib" -> "RanobeLib"
            "novelupdates" -> "Novel Updates"
            else -> siteName
                .split("_")
                .joinToString(" ") { word ->
                    word.replaceFirstChar { c -> c.uppercase() }
                }
        }
    }

    fun MediaExternalLinksQuery.ExternalLink.toDomain(): ExternalLinkData {
        return ExternalLinkData(
            icon = icon,
            siteName = site,
            url = url ?: ""
        )
    }
}