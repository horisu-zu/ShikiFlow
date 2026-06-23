package com.example.shikiflow.domain.model.common

enum class ScoreFormat {
    POINT_100,
    POINT_10_DECIMAL,
    POINT_10,
    POINT_5,
    POINT_3
}

data class FloatRange(
    val first: Float,
    val last: Float,
    val step: Float
)