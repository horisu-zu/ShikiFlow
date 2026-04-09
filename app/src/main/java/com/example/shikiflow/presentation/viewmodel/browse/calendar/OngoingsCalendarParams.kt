package com.example.shikiflow.presentation.viewmodel.browse.calendar

import com.example.shikiflow.domain.model.auth.AuthType
import kotlinx.datetime.DayOfWeek

data class OngoingsCalendarParams(
    val onList: Boolean = false,
    val currentDay: DayOfWeek? = null,
    val authType: AuthType? = null
)
