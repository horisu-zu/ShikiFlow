package com.example.shikiflow.data.mapper

import com.example.graphql.fragment.AnimeShort
import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.MangaKindEnum
import com.example.graphql.type.MangaStatusEnum
import com.example.graphql.type.RelationKindEnum
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.tracks.MediaType
import kotlin.reflect.KClass

object EnumUtils {
    fun formatEnumName(enumValue: Enum<*>): String =
        enumValue.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }

    private fun <T : Enum<T>> getEnumList(enumClass: KClass<T>): List<T> =
        enumClass.java.enumConstants
            ?.filter { !it.name.startsWith("UNKNOWN") }
            ?: emptyList()

    fun <T : Enum<T>> getFormattedEnumList(enumClass: KClass<T>): List<String> =
        getEnumList(enumClass).map { formatEnumName(it) }

    fun <T : Enum<T>> findEnumByFormattedName(enumClass: KClass<T>, formattedName: String): T? =
        getEnumList(enumClass).find { formatEnumName(it) == formattedName }
}

class UserRateMapper {
    companion object {
        fun mapStringToStatus(tab: String): UserRateStatusEnum? {
            return mapOf(
                "Watching" to UserRateStatusEnum.watching,
                "Planned" to UserRateStatusEnum.planned,
                "Completed" to UserRateStatusEnum.completed,
                "Rewatching" to UserRateStatusEnum.rewatching,
                "On Hold" to UserRateStatusEnum.on_hold,
                "Dropped" to UserRateStatusEnum.dropped
            )[tab]
        }

        fun mapStatusToString(status: UserRateStatusEnum): String {
            return when(status) {
                UserRateStatusEnum.watching -> "Watching"
                UserRateStatusEnum.planned -> "Planned"
                UserRateStatusEnum.completed -> "Completed"
                UserRateStatusEnum.rewatching -> "Rewatching"
                UserRateStatusEnum.on_hold -> "On Hold"
                UserRateStatusEnum.dropped -> "Dropped"
                UserRateStatusEnum.UNKNOWN__ -> "Unknown"
            }
        }

        fun mapStatusToString(
            status: UserRateStatusEnum,
            watchedEpisodes: Int? = null,
            allEpisodes: Int? = null,
            score: Int? = null,
            mediaType: MediaType = MediaType.ANIME
        ): String {
            val watchingVerb = if (mediaType == MediaType.ANIME) "Watching" else "Reading"
            val rewatchVerb = if (mediaType == MediaType.ANIME) "Rewatching" else "Rereading"

            return when (status) {
                UserRateStatusEnum.watching ->
                    if (watchedEpisodes != null) "$watchingVerb ∙ $watchedEpisodes/$allEpisodes"
                    else watchingVerb
                UserRateStatusEnum.planned -> "Planned"
                UserRateStatusEnum.completed ->
                    if (score != null) "Completed ∙ $score ★" else "Completed"
                UserRateStatusEnum.rewatching -> rewatchVerb
                UserRateStatusEnum.on_hold ->
                    if (watchedEpisodes != null) "On Hold ∙ $watchedEpisodes/$allEpisodes"
                    else "On Hold"
                UserRateStatusEnum.dropped ->
                    if (score != null && score != 0) "Dropped ∙ $score ★"
                    else if(watchedEpisodes != null) "Dropped ∙ $watchedEpisodes/$allEpisodes"
                    else "Dropped"
                UserRateStatusEnum.UNKNOWN__ -> "Add to List"
            }
        }

        fun isWatched(status: UserRateStatusEnum): Boolean {
            return setOf(
                UserRateStatusEnum.watching,
                UserRateStatusEnum.dropped,
                UserRateStatusEnum.on_hold
            ).contains(status)
        }

        fun mapAnimeStatus(status: AnimeStatusEnum?): String {
            return when(status) {
                AnimeStatusEnum.anons -> "Announced"
                AnimeStatusEnum.ongoing -> "Ongoing"
                AnimeStatusEnum.released -> "Released"
                else -> "Unknown"
            }
        }

        fun mapMangaStatus(status: MangaStatusEnum?): String {
            return when(status) {
                MangaStatusEnum.anons -> "Announced"
                MangaStatusEnum.ongoing -> "Ongoing"
                MangaStatusEnum.released -> "Released"
                MangaStatusEnum.paused -> "Paused"
                MangaStatusEnum.discontinued -> "Discontinued"
                else -> "Unknown"
            }
        }

        fun mapAnimeKind(status: AnimeKindEnum?): String {
            return when(status) {
                AnimeKindEnum.tv -> "TV"
                AnimeKindEnum.ona -> "ONA"
                AnimeKindEnum.ova -> "OVA"
                AnimeKindEnum.cm -> "CM"
                AnimeKindEnum.pv -> "PV"
                AnimeKindEnum.movie -> "Movie"
                AnimeKindEnum.music -> "Music"
                AnimeKindEnum.special -> "Special"
                AnimeKindEnum.tv_special -> "TV Special"
                else -> "Unknown"
            }
        }

        fun mapMangaKind(kind: MangaKindEnum?): String {
            return when(kind) {
                MangaKindEnum.manga -> "Manga"
                MangaKindEnum.manhwa -> "Manhwa"
                MangaKindEnum.manhua -> "Manhua"
                MangaKindEnum.light_novel -> "Ranobe"
                MangaKindEnum.novel -> "Novel"
                MangaKindEnum.one_shot -> "One Shot"
                MangaKindEnum.doujin -> "Doujin"
                else -> "Unknown"
            }
        }

        fun mapRelationKind(kind: RelationKindEnum?): String {
            return when(kind) {
                RelationKindEnum.adaptation -> "Adaptation"
                RelationKindEnum.alternative_setting -> "Alternative Setting"
                RelationKindEnum.alternative_version -> "Alternative version"
                RelationKindEnum.character -> "Character"
                RelationKindEnum.full_story -> "Full Story"
                RelationKindEnum.other -> "Other"
                RelationKindEnum.parent_story -> "Parent Story"
                RelationKindEnum.prequel -> "Prequel"
                RelationKindEnum.sequel -> "Sequel"
                RelationKindEnum.side_story -> "Side Story"
                RelationKindEnum.spin_off -> "Spin Off"
                RelationKindEnum.summary -> "Summary"
                else -> "Unknown"
            }
        }

        fun determineSeason(airedOn: AnimeShort.AiredOn?): String {
            val year = airedOn?.year
            val month = airedOn?.month

            return when {
                year == null && month == null -> "Unknown"
                year != null && month == null -> year.toString()
                month in 3..5 -> "Spring $year"
                month in 6..8 -> "Summer $year "
                month in 9..11 -> "Autumn $year "
                month in listOf(1, 2, 12) -> "Winter $year"
                else -> "Unknown"
            }
        }
    }
}