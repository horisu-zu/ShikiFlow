package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.fragment.ALRateEntry
import com.example.graphql.anilist.fragment.ALRateEntryShort
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import kotlin.time.Instant

object AnilistRateMapper {
    fun ALRateEntry.toDomain(): UserMediaRate {
        return UserMediaRate(
            rateId = this.id,
            mediaId = this.mediaId,
            rateStatus = this.status?.toDomain() ?: UserRateStatus.UNKNOWN,
            progress = this.progress ?: 0,
            progressVolumes = this.progressVolumes ?: 0,
            repeat = this.repeat ?: 0,
            textNotes = this.notes,
            score = this.score.score?.toInt() ?: 0,
            createdAt = Instant.fromEpochSeconds(this.createdAt?.toLong() ?: 0L),
            updatedAt = Instant.fromEpochSeconds(this.updatedAt?.toLong() ?: 0L)
        )
    }

    fun ALRateEntryShort.toDomain(): ShortUserMediaRate {
        return ShortUserMediaRate(
            id = this.mediaId,
            title = this.media?.title?.romaji ?: "",
            imageUrl = this.media?.coverImage?.large ?: "",
            score = this.score.score?.toInt() ?: 0,
            status = this.status?.toDomain() ?: UserRateStatus.UNKNOWN,
            progress = this.progress ?: 0
        )
    }
}