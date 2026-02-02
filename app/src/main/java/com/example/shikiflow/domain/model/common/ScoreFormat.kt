package com.example.shikiflow.domain.model.common

enum class ScoreFormat(val minVal: Int, val maxVal: Int, val step: Int) {
    POINT_10(minVal = 1, maxVal = 10, step = 1),
    POINT_100_STEP_5(minVal = 5, maxVal = 100, step = 5),
    POINT_100_STEP_10(minVal = 10, maxVal = 100, step = 10);

    companion object {
        fun detectFormat(scoreStats: Map<Int, Int>): ScoreFormat {
            val maxScore = scoreStats.keys.maxOrNull() ?: 0

            val step = when {
                maxScore <= 10 -> 1
                scoreStats.keys.any { it % 5 != 0 } -> 10
                scoreStats.keys.any { it % 10 != 0 } -> 5
                else -> 10
            }

            return when {
                maxScore <= 10 -> POINT_10
                maxScore > 10 && step == 5 -> POINT_100_STEP_5
                else -> POINT_100_STEP_10
            }
        }
    }
}