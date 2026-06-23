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

    fun ScoreFormat.toAnilistFormat(): ALScoreFormat {
        return when(this) {
            ScoreFormat.POINT_100 -> ALScoreFormat.POINT_100
            ScoreFormat.POINT_10_DECIMAL -> ALScoreFormat.POINT_10_DECIMAL
            ScoreFormat.POINT_10 -> ALScoreFormat.POINT_10
            ScoreFormat.POINT_5 -> ALScoreFormat.POINT_5
            ScoreFormat.POINT_3 -> ALScoreFormat.POINT_3
        }
    }

    fun ScoreFormat.formatShikimoriValue(score: Float): Float {
        return when(this) {
            ScoreFormat.POINT_100 -> score * 10
            ScoreFormat.POINT_10_DECIMAL, ScoreFormat.POINT_10 -> score
            ScoreFormat.POINT_5 -> score / 2
            ScoreFormat.POINT_3 -> score / 3
        }
    }

    fun ScoreFormat.formatAniListValue(score: Float): Float {
        return when(this) {
            ScoreFormat.POINT_100 -> score / 10
            ScoreFormat.POINT_10_DECIMAL, ScoreFormat.POINT_10 -> score
            ScoreFormat.POINT_5 -> score * 2
            ScoreFormat.POINT_3 -> when {
                score >= 3f -> 9f
                score >= 2f -> 6f
                score >= 1f -> 3f
                else -> 0f
            }
        }
    }
}