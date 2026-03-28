package com.example.shikiflow.presentation.viewmodel.browse.calendar

import kotlinx.datetime.DayOfWeek

data class OngoingsCalendarParams(
    val onList: Boolean = false,
    val currentDay: DayOfWeek? = null
)
