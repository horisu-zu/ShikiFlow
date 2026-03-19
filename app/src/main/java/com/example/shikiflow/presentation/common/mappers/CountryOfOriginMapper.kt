package com.example.shikiflow.presentation.common.mappers

import androidx.compose.ui.graphics.Color
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.CountryOfOrigin

object CountryOfOriginMapper {
    fun CountryOfOrigin.displayValue(): Int {
        return when(this) {
            CountryOfOrigin.JAPAN -> R.string.country_japan
            CountryOfOrigin.SOUTH_KOREA -> R.string.country_south_korea
            CountryOfOrigin.CHINA -> R.string.country_china
            CountryOfOrigin.TAIWAN -> R.string.country_taiwan
        }
    }

    fun CountryOfOrigin.color(): Color {
        return when(this) {
            CountryOfOrigin.JAPAN -> Color(0xFFAE62CF)
            CountryOfOrigin.SOUTH_KOREA -> Color(0xFF62CF71)
            CountryOfOrigin.CHINA -> Color(0xFFCF6562)
            CountryOfOrigin.TAIWAN -> Color(0xFF628ACF)
        }
    }
}