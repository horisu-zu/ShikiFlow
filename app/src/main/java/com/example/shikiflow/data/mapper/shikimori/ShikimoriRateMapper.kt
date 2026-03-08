package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.fragment.UserRateShort
import com.example.graphql.shikimori.type.UserRateStatusEnum
import com.example.shikiflow.data.datasource.dto.ShikiUserRateResponse
import com.example.shikiflow.data.datasource.dto.ShikiShortUserRate
import com.example.shikiflow.data.datasource.dto.ShikiShortUserRate.Companion.getImageUrl
import com.example.shikiflow.data.datasource.dto.ShikiShortUserRate.Companion.getMediaId
import com.example.shikiflow.data.datasource.dto.ShikiShortUserRate.Companion.getMediaTitle
import com.example.shikiflow.data.datasource.dto.ShikiShortUserRate.Companion.getProgress
import com.example.shikiflow.data.datasource.dto.ShikiShortUserRateResponse
import com.example.shikiflow.data.datasource.dto.ShikiTargetType.Companion.toMediaType
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.model.tracks.UserRate
import com.example.shikiflow.domain.model.user.MediaTypeStats
import com.example.shikiflow.domain.model.user.UserRateStats
import kotlin.time.Instant

object ShikimoriRateMapper {
    fun ShikiShortUserRateResponse.toDomain(): UserRate {
        return UserRate(
            id = this.id.toInt(),
            status = UserRateStatusEnum.valueOf(this.status).toDomain(),
            score = this.score,
            mediaType = this.shikiTargetType.toMediaType()
        )
    }

    fun ShikiUserRateResponse.toDomain(mediaType: MediaType): UserMediaRate {
        return UserMediaRate(
            rateId = this.id.toInt(),
            mediaId = this.targetId.toInt(),
            rateStatus = UserRateStatusEnum.valueOf(this.status).toDomain(),
            progress = when(mediaType) {
                MediaType.ANIME -> this.episodes
                MediaType.MANGA -> this.chapters
            } ?: 0,
            progressVolumes = this.volumes ?: 0,
            repeat = this.rewatches,
            textNotes = this.text,
            score = this.score,
            createdAt = Instant.parse(this.createdAt),
            updatedAt = Instant.parse(this.updatedAt)
        )
    }

    fun ShikiShortUserRate.toDomain(): ShortUserMediaRate {
        return ShortUserMediaRate(
            id = this.getMediaId().toInt(),
            title = this.getMediaTitle(),
            imageUrl = this.getImageUrl() ?: "",
            score = this.score,
            status = this.status.toDomain(),
            progress = this.getProgress()
        )
    }

    fun List<UserRate>.toDomain(): UserRateStats {
        return UserRateStats(
            mediaStats = this.groupBy { it.mediaType }
                .mapValues { (_, rates) ->
                    MediaTypeStats(
                        count = rates.size,
                        averageScore = rates.map { it.score }.average(),
                        statusesStats = rates.groupingBy { it.status }
                            .eachCount()
                            .toSortedMap(compareBy { UserRateStatus.entries.indexOf(it) }),
                        scoreStats = rates.filter { it.status == UserRateStatus.COMPLETED }
                            .groupingBy { it.score }
                            .eachCount()
                            .toSortedMap(comparator = compareBy { it })
                    )
                }
        )
    }

    fun UserRateShort.toDomain(mediaId: Int, mediaType: MediaType): UserMediaRate {
        return UserMediaRate(
            rateId = this.id.toInt(),
            mediaId = mediaId,
            rateStatus = this.status.toDomain(),
            progress = when(mediaType) {
                MediaType.ANIME -> this.episodes
                MediaType.MANGA -> this.chapters
            },
            progressVolumes = this.volumes,
            repeat = this.rewatches,
            textNotes = this.text,
            score = this.score,
            createdAt = Instant.parse(this.createdAt.toString()),
            updatedAt = Instant.parse(this.updatedAt.toString())
        )
    }
}