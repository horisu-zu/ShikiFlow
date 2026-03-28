package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.Date

object SeasonMapper {
    fun determineSeason(month: Int?): Int? {
        return when(month) {
            in 1..3 -> R.string.season_winter
            in 4..6 -> R.string.season_spring
            in 7..9 -> R.string.season_summer
            in 10..12 -> R.string.season_fall
            else -> null
        }
    }
}