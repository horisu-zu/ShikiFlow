package com.example.shikiflow.presentation.common.mappers

import androidx.compose.ui.graphics.Color
import com.example.shikiflow.domain.model.track.UserRateStatus

object ColorMapper {
    fun UserRateStatus.color(): Color {
        return when(this) {
            UserRateStatus.WATCHING -> Color(0xFFAE62CF)
            UserRateStatus.PLANNED -> Color(0xFFC2B75A)
            UserRateStatus.REWATCHING -> Color(0xFF62CFCF)
            UserRateStatus.COMPLETED -> Color(0xFF62CF71)
            UserRateStatus.PAUSED -> Color(0xFF628ACF)
            UserRateStatus.DROPPED -> Color(0xFFCF6562)
            UserRateStatus.UNKNOWN -> Color(0xFF8C8C8C)
        }
    }

    fun getMangaDexStatusColor(status: String): Color = when(status) {
        "hiatus" -> Color(0xFFDA7500)
        "ongoing" -> Color(0xFF04D000)
        "completed" -> Color(0xFF00C9F5)
        "cancelled" -> Color(0xFFFF4040)
        else -> Color(0xFF8C8C8C)
    }

    fun getRatioColor(
        ratio: Float
    ): Color {
        return when {
            ratio < 1 / 3f -> Color(0xFFCF6562)
            ratio < 2 / 3f -> Color(0xFFC2B75A)
            else -> Color(0xFF62CF71)
        }
    }
}