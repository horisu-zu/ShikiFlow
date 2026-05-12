package com.example.shikiflow.presentation.screen.browse.ongoings

import androidx.compose.ui.graphics.Color
import com.example.shikiflow.utils.DateUtils.timeDifference
import kotlin.time.Duration
import kotlin.time.Instant

enum class AiringStatus {
    AIRED,
    AIRING,
    NOT_YET_AIRED;

    companion object {
        fun AiringStatus.color(): Color {
            return when(this) {
                AIRED -> Color(0xFF62CF71)
                AIRING -> Color(0xFFCF6562)
                NOT_YET_AIRED -> Color(0xFF8C8C8C)
            }
        }

        fun Instant?.status(duration: Duration): AiringStatus {
            val timeDiff = this?.timeDifference() ?: return AIRED

            return when {
                timeDiff > Duration.ZERO -> NOT_YET_AIRED
                timeDiff >= -duration -> AIRING
                else -> AIRED
            }
        }
    }
}