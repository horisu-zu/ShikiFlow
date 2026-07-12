package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.fragment.ALRateEntry
import com.example.graphql.anilist.fragment.ALRateEntryShort
import com.example.shikiflow.data.mapper.common.GenreMapper
import com.example.shikiflow.data.mapper.common.MediaTitleMapper.toDomainTitle
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.TagMapper
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import kotlin.math.roundToInt
import kotlin.time.Instant

object AnilistRateMapper {
    fun ALRateEntry.toDomain(): UserMediaRate {
        return UserMediaRate(
            rateId = id,
            mediaId = mediaId,
            rateStatus = status?.toDomain() ?: UserRateStatus.UNKNOWN,
            progress = progress ?: 0,
            progressVolumes = progressVolumes ?: 0,
            repeat = repeat ?: 0,
            textNotes = notes,
            score = (score.score?.times(10))?.roundToInt() ?: 0,
            createdAt = Instant.fromEpochSeconds(createdAt?.toLong() ?: 0L),
            updatedAt = Instant.fromEpochSeconds(updatedAt?.toLong() ?: 0L)
        )
    }

    fun ALRateEntryShort.toShortUserMediaRate(): ShortUserMediaRate {
        return ShortUserMediaRate(
            id = mediaId,
            title = media?.title?.mediaTitle.toDomainTitle(),
            synonyms = media?.synonyms?.filterNotNull().orEmpty(),
            imageUrl = media?.coverImage?.large ?: "",
            score = score?.toInt() ?: 0,
            status = status?.toDomain() ?: UserRateStatus.UNKNOWN,
            progress = progress ?: 0,
            genres = media?.genres?.let { genres ->
                genres.mapNotNull { genre ->
                    genre?.let {
                        GenreMapper.fromString(genre)
                    }
                }
            } ?: emptyList(),
            tags = media?.tags?.let { tags ->
                tags.mapNotNull { tag ->
                    tag?.let {
                        TagMapper.fromString(tag.name)
                    }
                }
            } ?: emptyList()
        )
    }
}