package com.example.shikiflow.utils

import androidx.compose.ui.graphics.Color
import com.example.shikiflow.domain.model.track.UserRateStatus

object StatusColor {
    fun getAnimeStatusColor(status: UserRateStatus): Color = when(status) {
        UserRateStatus.WATCHING -> Color(0xFFAE62CF)
        UserRateStatus.PLANNED -> Color(0xFFD4C862)
        UserRateStatus.REWATCHING -> Color(0xFF62CFCF)
        UserRateStatus.COMPLETED -> Color(0xFF62CF71)
        UserRateStatus.PAUSED -> Color(0xFF628ACF)
        UserRateStatus.DROPPED -> Color(0xFFCF6562)
        UserRateStatus.UNKNOWN -> Color(0xFF8C8C8C)
    }

    fun getMangaDexStatusColor(status: String): Color = when(status) {
        "hiatus" -> Color(0xFFDA7500)
        "ongoing" -> Color(0xFF04D000)
        "completed" -> Color(0xFF00C9F5)
        "cancelled" -> Color(0xFFFF4040)
        else -> Color(0xFF8C8C8C)
    }
}