package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaSeasonEnum
import com.example.shikiflow.utils.IconResource

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

    fun MediaSeasonEnum.displayValue(): Int {
        return when(this) {
            MediaSeasonEnum.WINTER -> R.string.season_winter
            MediaSeasonEnum.SPRING -> R.string.season_spring
            MediaSeasonEnum.SUMMER -> R.string.season_summer
            MediaSeasonEnum.FALL -> R.string.season_fall
        }
    }

    fun MediaSeasonEnum.iconResource(): IconResource {
        return when(this) {
            MediaSeasonEnum.WINTER -> IconResource.Drawable(resId = R.drawable.ic_snowflake)
            MediaSeasonEnum.SPRING -> IconResource.Drawable(resId = R.drawable.ic_flower)
            MediaSeasonEnum.SUMMER -> IconResource.Drawable(resId = R.drawable.ic_sun)
            MediaSeasonEnum.FALL -> IconResource.Drawable(resId = R.drawable.ic_rain)
        }
    }
}