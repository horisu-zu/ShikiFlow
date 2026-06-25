package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.FloatRange
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.utils.IconResource
import kotlin.math.roundToInt

object ScoreFormatMapper {
    fun ScoreFormat.maxValue(): Int {
        return valueRange().last.roundToInt()
    }

    fun ScoreFormat.displayValue(): Int {
        return when (this) {
            ScoreFormat.POINT_100 -> R.string.score_format_100_point
            ScoreFormat.POINT_10_DECIMAL -> R.string.score_format_10_point_decimal
            ScoreFormat.POINT_10 -> R.string.score_format_10_point
            ScoreFormat.POINT_5 -> R.string.score_format_5_point
            ScoreFormat.POINT_3 -> R.string.score_format_3_point_smiley
        }
    }

    fun ScoreFormat.formatValue(score: Float): Float {
        return when (this) {
            ScoreFormat.POINT_100 -> score
            ScoreFormat.POINT_10_DECIMAL,
            ScoreFormat.POINT_10 -> score / 10
            ScoreFormat.POINT_5 -> (score + 10) / 20
            ScoreFormat.POINT_3 -> when (score.roundToInt()) {
                in 70..100 -> 3f
                in 50..70 -> 2f
                else -> 1f
            }
            //ScoreFormat.POINT_3 -> (score / 33).roundToInt().toFloat()
        }
    }

    fun ScoreFormat.displayValue(score: Float): String {
        return when (this) {
            ScoreFormat.POINT_100,
            ScoreFormat.POINT_10,
            ScoreFormat.POINT_5 -> score.roundToInt().toString()
            ScoreFormat.POINT_10_DECIMAL -> if (score % 1 == 0f) score.roundToInt().toString() else "%.1f".format(score)
            ScoreFormat.POINT_3 -> "-"
        }
    }

    fun smileyIcon(score: Float): IconResource {
        return when (score) {
            3f -> IconResource.Drawable(R.drawable.ic_satisfied)
            2f -> IconResource.Drawable(R.drawable.ic_neutral)
            else -> IconResource.Drawable(R.drawable.ic_dissatisfied)
        }
    }

    fun ScoreFormat.valueRange(): FloatRange {
        return when (this) {
            ScoreFormat.POINT_100 -> FloatRange(0f, 100f, 1f)
            ScoreFormat.POINT_10_DECIMAL -> FloatRange(0f, 10f, 0.1f)
            ScoreFormat.POINT_10 -> FloatRange(0f, 10f, 1f)
            ScoreFormat.POINT_5 -> FloatRange(0f, 5f, 1f)
            ScoreFormat.POINT_3 -> FloatRange(0f, 3f, 1f)
        }
    }

    fun FloatRange.floatingPointRange() = first..last

    fun FloatRange.count() = ((last - first) / step).roundToInt() + 1

    fun FloatRange.steps() = count() - 2
}