package com.example.shikiflow.domain.model.track

import com.example.graphql.fragment.AnimeShort
import com.example.graphql.fragment.MangaShort
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

data class ReleaseDate(
    val year: Int?,
    val month: Int?,
    val day: Int?,
    val date: Instant?
) {
    companion object {
        fun AnimeShort.AiredOn.toEntity(): ReleaseDate {
            return ReleaseDate(
                year = this.year,
                month = this.month,
                day = this.day,
                date = this.date?.let { LocalDate.parse(it.toString())
                    .atStartOfDayIn(TimeZone.currentSystemDefault()) }
            )
        }

        fun AnimeShort.ReleasedOn.toEntity(): ReleaseDate {
            return ReleaseDate(
                year = this.year,
                month = this.month,
                day = this.day,
                date = this.date?.let { LocalDate.parse(it.toString())
                    .atStartOfDayIn(TimeZone.currentSystemDefault()) }
            )
        }

        fun MangaShort.AiredOn.toEntity(): ReleaseDate {
            return ReleaseDate(
                year = this.year,
                month = this.month,
                day = this.day,
                date = this.date?.let { LocalDate.parse(it.toString())
                    .atStartOfDayIn(TimeZone.currentSystemDefault()) }
            )
        }

        fun MangaShort.ReleasedOn.toEntity(): ReleaseDate {
            return ReleaseDate(
                year = this.year,
                month = this.month,
                day = this.day,
                date = this.date?.let { LocalDate.parse(it.toString())
                    .atStartOfDayIn(TimeZone.currentSystemDefault()) }
            )
        }
    }
}
