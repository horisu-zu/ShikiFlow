package com.example.shikiflow.data.local.entity

import com.example.graphql.shikimori.fragment.DateShort
import com.example.shikiflow.domain.model.track.Date
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Instant

data class ReleaseDateEntity(
    val year: Int?,
    val month: Int?,
    val day: Int?,
    val date: Instant?
) {
    companion object {
        fun DateShort.toEntity(): ReleaseDateEntity {
            return ReleaseDateEntity(
                year = this.year,
                month = this.month,
                day = this.day,
                date = this.date?.let { LocalDate.parse(it.toString())
                    .atStartOfDayIn(TimeZone.currentSystemDefault()) }
            )
        }

        fun ReleaseDateEntity.toDomain(): Date {
            return Date(
                year = this.year,
                month = this.month,
                day = this.day,
                date = this.date
            )
        }

        fun Date.toDto(): ReleaseDateEntity {
            return ReleaseDateEntity(
                year = this.year,
                month = this.month,
                day = this.day,
                date = this.date
            )
        }
    }
}