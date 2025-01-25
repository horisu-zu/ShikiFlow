package com.example.shikiflow.data.mapper

import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.fragment.AnimeShort
import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.UserRateStatusEnum

class UserRateMapper {
    companion object {
        fun mapStatus(tab: String): UserRateStatusEnum? {
            return mapOf(
                "Watching" to UserRateStatusEnum.watching,
                "Planned" to UserRateStatusEnum.planned,
                "Watched" to UserRateStatusEnum.completed,
                "Rewatching" to UserRateStatusEnum.rewatching,
                "On Hold" to UserRateStatusEnum.on_hold,
                "Dropped" to UserRateStatusEnum.dropped
            )[tab]
        }

        fun mapAnimeStatus(status: AnimeStatusEnum?): String {
            return when(status) {
                AnimeStatusEnum.anons -> "Announced"
                AnimeStatusEnum.ongoing -> "Ongoing"
                AnimeStatusEnum.released -> "Released"
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