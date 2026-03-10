package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.AgeRating

object AgeRatingMapper {
    fun AgeRating.displayValue() : Int {
        return when(this) {
            AgeRating.RX -> R.string.rating_rx
            AgeRating.R_PLUS -> R.string.rating_r_plus
            AgeRating.R_17 -> R.string.rating_r_17
            AgeRating.PG_13 -> R.string.rating_pg_13
            AgeRating.PG -> R.string.rating_pg
            AgeRating.G -> R.string.rating_g
            AgeRating.NONE -> R.string.rating_none
            AgeRating.UNKNOWN -> R.string.common_unknown
        }
    }
}