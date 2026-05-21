package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.fragment.Date as ALDate
import com.example.graphql.shikimori.fragment.DateShort
import com.example.shikiflow.data.datasource.dto.person.ShikiDate
import com.example.shikiflow.domain.model.track.Date
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

object DateMapper {
    fun DateShort.toDomain(): Date? {
        return year?.let {
            Date(
                year = year,
                month = month,
                day = day,
                date = date?.let { LocalDate.parse(it.toString())
                    .atStartOfDayIn(TimeZone.currentSystemDefault()) }
            )
        }
    }

    fun ShikiDate.toLocalDate(): LocalDate? {
        return if(year != null && month != null && day != null) {
            LocalDate(year, month, day)
        } else null
    }

    fun ALDate.toDomain(): Date {
        return Date(
            year = year,
            month = month,
            day = day,
            date = if(year != null && month != null && day != null) {
                LocalDate(year, month, day).atStartOfDayIn(TimeZone.currentSystemDefault())
            } else null
        )
    }

    fun ALDate.toLocalDate(): LocalDate? {
        return if(year != null && month != null && day != null) {
            LocalDate(year, month, day)
        } else null
    }

    fun Int.minutesToDays(): Float = this / 60.0f / 24.0f
}