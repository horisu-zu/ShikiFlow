package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.fragment.Date as ALDate
import com.example.graphql.shikimori.fragment.DateShort
import com.example.shikiflow.data.datasource.dto.person.ShikiDate
import com.example.shikiflow.domain.model.track.Date
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

object DateMapper {
    fun DateShort.toDomain(): Date {
        return Date(
            year = this.year,
            month = this.month,
            day = this.day,
            date = this.date?.let { LocalDate.parse(it.toString())
                .atStartOfDayIn(TimeZone.currentSystemDefault()) }
        )
    }

    fun ShikiDate.toLocalDate(): LocalDate? {
        return year?.let {
            LocalDate(year, month ?: 0, day ?: 0)
        }
    }

    fun ALDate.toDomain(): Date {
        return Date(
            year = this.year,
            month = this.month,
            day = this.day,
            date = null
        )
    }

    fun ALDate.toLocalDate(): LocalDate? {
        return year?.let {
             LocalDate(year, month ?: 0, day ?: 0)
        }
    }
}