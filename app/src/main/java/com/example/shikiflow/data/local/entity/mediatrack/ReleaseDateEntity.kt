package com.example.shikiflow.data.local.entity.mediatrack

import com.example.shikiflow.domain.model.track.Date
import kotlin.time.Instant

data class ReleaseDateEntity(
    val year: Int?,
    val month: Int?,
    val day: Int?,
    val date: Instant?
) {
    companion object {
        fun ReleaseDateEntity.toDomain(): Date {
            return Date(
                year = this.year ?: 0,
                month = this.month ?: 0,
                day = this.day ?: 0,
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