package com.example.shikiflow.presentation.common.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
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
            ratio <= 1 / 3f -> Color(0xFFCF6562)
            ratio <= 2 / 3f -> Color(0xFFC2B75A)
            else -> Color(0xFF62CF71)
        }
    }

    fun Color.onColor(): Color {
        val luminance = ColorUtils.calculateLuminance(this.toArgb())

        return if (luminance < 0.3f) Color.White
            else Color(0xFF1A1A1A)
    }

    fun getPickerQuickColors(): List<Color> {
        return listOf(
            Color(0xFFF44336),
            Color(0xFFFF5722),
            Color(0xFFFF9800),
            Color(0xFFFFC107),
            Color(0xFFFFEB3B),
            Color(0xFFCDDC39),
            Color(0xFF8BC34A),
            Color(0xFF4CAF50),
            Color(0xFF009688),
            Color(0xFF00BCD4),
            Color(0xFF2196F3),
            Color(0xFF3F51B5),
            Color(0xFF673AB7),
            Color(0xFF9C27B0),
            Color(0xFFE91E63),
        )
    }

    fun Color.lerp(
        startColor: Color,
        fraction: Float = 0.3f
    ): Color {
        return lerp(startColor, this, fraction)
    }
}