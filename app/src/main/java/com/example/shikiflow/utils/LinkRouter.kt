package com.example.shikiflow.utils

import com.example.shikiflow.domain.model.comment.EntityData
import com.example.shikiflow.domain.model.comment.EntityType

object LinkRouter {
    private val anilistLinkRegex = Regex(
        pattern = """https?://anilist\.co/(anime|manga|character|staff|user|activity)/([0-9]+)(?:/([A-Za-z0-9][A-Za-z0-9-]*))?/?"""
    )
    private val shikimoriLinkRegex = Regex(
        pattern = """https?://shikimori\.[a-z]+/([a-z]+)/([0-9]+)[^\s]*"""
    )

    fun getEntityData(url: String): EntityData? {
        val anilistMatch = anilistLinkRegex.matchEntire(url)
        if (anilistMatch != null) {
            val type = when (anilistMatch.groupValues[1]) {
                "anime" -> EntityType.ANIME
                "manga" -> EntityType.MANGA
                "character" -> EntityType.CHARACTER
                "staff" -> EntityType.PERSON
                else -> return null
            }
            return EntityData(id = anilistMatch.groupValues[2], type = type)
        }

        val shikimoriMatch = shikimoriLinkRegex.matchEntire(url)
        if (shikimoriMatch != null) {
            val type = when (shikimoriMatch.groupValues[1]) {
                "animes" -> EntityType.ANIME
                "mangas" -> EntityType.MANGA
                "ranobe" -> EntityType.RANOBE
                "characters" -> EntityType.CHARACTER
                "people" -> EntityType.PERSON
                "comments" -> EntityType.COMMENT
                else -> return null
            }
            return EntityData(id = shikimoriMatch.groupValues[2], type = type)
        }

        return null
    }
}