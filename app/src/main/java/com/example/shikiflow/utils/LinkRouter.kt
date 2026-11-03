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
                "anime" -> EntityType.Anime
                "manga" -> EntityType.Manga
                "character" -> EntityType.Character
                "staff" -> EntityType.Staff
                else -> return null
            }
            return EntityData(id = anilistMatch.groupValues[2], type = type)
        }

        val shikimoriMatch = shikimoriLinkRegex.matchEntire(url)
        if (shikimoriMatch != null) {
            val type = when (shikimoriMatch.groupValues[1]) {
                "animes" -> EntityType.Anime
                "mangas" -> EntityType.Manga
                "ranobe" -> EntityType.Ranobe
                "characters" -> EntityType.Character
                "people" -> EntityType.Staff
                "comments" -> EntityType.CommentReply
                else -> return null
            }
            return EntityData(id = shikimoriMatch.groupValues[2], type = type)
        }

        return null
    }
}