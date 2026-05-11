package com.example.shikiflow.presentation.screen.browse.ongoings

import androidx.compose.ui.graphics.Color
import kotlin.time.Duration

enum class AiringStatus {
    AIRED,
    AIRING,
    NOT_YET_AIRED;

    companion object {
        fun AiringStatus.color(): Color {
            return when(this) {
                AIRED -> Color(0xFF62CF71)
                AIRING -> Color(0xFFCF6562)
                NOT_YET_AIRED -> Color(0xFF628ACF)
            }
        }

        fun Duration?.status(duration: Duration): AiringStatus {
            return when {
                this == null -> AIRED
                this in -duration..Duration.ZERO -> AIRING
                this < Duration.ZERO -> AIRED
                else -> NOT_YET_AIRED
            }
        }
    }
}