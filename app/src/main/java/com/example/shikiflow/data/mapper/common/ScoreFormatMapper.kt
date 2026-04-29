package com.example.shikiflow.data.mapper.common

import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.graphql.anilist.type.ScoreFormat as ALScoreFormat

object ScoreFormatMapper {
    fun ALScoreFormat.toDomainFormat(): ScoreFormat {
        return when(this) {
            ALScoreFormat.POINT_100 -> ScoreFormat.POINT_100
            ALScoreFormat.POINT_10_DECIMAL -> ScoreFormat.POINT_10_DECIMAL
            ALScoreFormat.POINT_10 -> ScoreFormat.POINT_10
            ALScoreFormat.POINT_5 -> ScoreFormat.POINT_5
            ALScoreFormat.POINT_3 -> ScoreFormat.POINT_3
            ALScoreFormat.UNKNOWN__ -> ScoreFormat.POINT_10
        }
    }
}