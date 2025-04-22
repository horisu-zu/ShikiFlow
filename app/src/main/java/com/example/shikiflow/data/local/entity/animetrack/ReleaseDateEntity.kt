package com.example.shikiflow.data.local.entity.animetrack

import com.example.graphql.fragment.AnimeShort
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

data class ReleaseDateEntity(
    val year: Int?,
    val month: Int?,
    val day: Int?,
    val date: Instant?
) {
    companion object {
        fun AnimeShort.AiredOn.toEntity(): ReleaseDateEntity {
            return ReleaseDateEntity(
                year = this.year,
                month = this.month,
                day = this.day,
                date = this.date?.let { LocalDate.parse(it.toString())
                    .atStartOfDayIn(TimeZone.currentSystemDefault()) }
            )
        }

        fun AnimeShort.ReleasedOn.toEntity(): ReleaseDateEntity {
            return ReleaseDateEntity(
                year = this.year,
                month = this.month,
                day = this.day,
                date = this.date?.let { LocalDate.parse(it.toString())
                    .atStartOfDayIn(TimeZone.currentSystemDefault()) }
            )
        }
    }
}