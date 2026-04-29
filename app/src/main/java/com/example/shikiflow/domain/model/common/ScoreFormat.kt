package com.example.shikiflow.domain.model.common

enum class ScoreFormat(val maxVal: Int) {
    POINT_100(maxVal = 100),
    POINT_10_DECIMAL(maxVal = 10),
    POINT_10(maxVal = 10),
    POINT_5(maxVal = 5),
    POINT_3(maxVal = 3)
}