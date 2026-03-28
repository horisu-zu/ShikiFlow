package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import kotlinx.datetime.DayOfWeek

object DateMapper {
    fun DayOfWeek.displayValue(): Int {
        return when(this) {
            DayOfWeek.MONDAY -> R.string.day_of_week_monday
            DayOfWeek.TUESDAY -> R.string.day_of_week_tuesday
            DayOfWeek.WEDNESDAY -> R.string.day_of_week_wednesday
            DayOfWeek.THURSDAY -> R.string.day_of_week_thursday
            DayOfWeek.FRIDAY -> R.string.day_of_week_friday
            DayOfWeek.SATURDAY -> R.string.day_of_week_saturday
            DayOfWeek.SUNDAY -> R.string.day_of_week_sunday
        }
    }
}