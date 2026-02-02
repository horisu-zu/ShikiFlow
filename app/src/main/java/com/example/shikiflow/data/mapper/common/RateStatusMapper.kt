package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.MediaListStatus
import com.example.graphql.shikimori.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.track.UserRateStatus

object RateStatusMapper {
    fun UserRateStatusEnum.toDomain(): UserRateStatus {
        return when(this) {
            UserRateStatusEnum.planned -> UserRateStatus.PLANNED
            UserRateStatusEnum.watching -> UserRateStatus.WATCHING
            UserRateStatusEnum.rewatching -> UserRateStatus.REWATCHING
            UserRateStatusEnum.completed -> UserRateStatus.COMPLETED
            UserRateStatusEnum.on_hold -> UserRateStatus.PAUSED
            UserRateStatusEnum.dropped -> UserRateStatus.DROPPED
            UserRateStatusEnum.UNKNOWN__ -> UserRateStatus.UNKNOWN
        }
    }

    fun UserRateStatus.toShikimoriRateStatus(): UserRateStatusEnum {
        return when(this) {
            UserRateStatus.WATCHING -> UserRateStatusEnum.watching
            UserRateStatus.PLANNED -> UserRateStatusEnum.planned
            UserRateStatus.COMPLETED -> UserRateStatusEnum.completed
            UserRateStatus.REWATCHING -> UserRateStatusEnum.rewatching
            UserRateStatus.PAUSED -> UserRateStatusEnum.on_hold
            UserRateStatus.DROPPED -> UserRateStatusEnum.dropped
            UserRateStatus.UNKNOWN -> UserRateStatusEnum.UNKNOWN__
        }
    }

    fun MediaListStatus.toDomain(): UserRateStatus {
        return when(this) {
            MediaListStatus.CURRENT -> UserRateStatus.WATCHING
            MediaListStatus.PLANNING -> UserRateStatus.PLANNED
            MediaListStatus.COMPLETED -> UserRateStatus.COMPLETED
            MediaListStatus.DROPPED -> UserRateStatus.DROPPED
            MediaListStatus.PAUSED -> UserRateStatus.PAUSED
            MediaListStatus.REPEATING -> UserRateStatus.REWATCHING
            MediaListStatus.UNKNOWN__ -> UserRateStatus.UNKNOWN
        }
    }

    fun UserRateStatus.toAnilistRateStatus(): MediaListStatus {
        return when(this) {
            UserRateStatus.WATCHING -> MediaListStatus.CURRENT
            UserRateStatus.PLANNED -> MediaListStatus.PLANNING
            UserRateStatus.COMPLETED -> MediaListStatus.COMPLETED
            UserRateStatus.REWATCHING -> MediaListStatus.REPEATING
            UserRateStatus.PAUSED -> MediaListStatus.PAUSED
            UserRateStatus.DROPPED -> MediaListStatus.DROPPED
            UserRateStatus.UNKNOWN -> MediaListStatus.UNKNOWN__
        }
    }
}