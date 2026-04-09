package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.fragment.UserRateShort
import com.example.graphql.shikimori.type.UserRateStatusEnum
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.ShikiUserRateResponse
import com.example.shikiflow.data.datasource.dto.media.ShikiShortUserRate
import com.example.shikiflow.data.datasource.dto.ShikiShortUserRateResponse
import com.example.shikiflow.data.datasource.dto.ShikiTargetType.Companion.toMediaType
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.model.tracks.UserRate
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.OverviewStatType
import com.example.shikiflow.domain.model.user.stats.ShortOverviewStat
import com.example.shikiflow.domain.model.user.stats.Stat
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
        return when(this) {
            is ShikiShortUserRate.ShikiShortAnimeRate -> {
                ShortUserMediaRate(
                    id = anime.id.toInt(),
                    title = anime.name,
                    ruTitle = anime.russian,
                    imageUrl = "${BuildConfig.SHIKI_BASE_URL}${anime.image.x96}",
                    score = score,
                    status = status.toDomain(),
                    progress = anime.episodes
                )
            }
            is ShikiShortUserRate.ShikiShortMangaRate -> {
                ShortUserMediaRate(
                    id = manga.id.toInt(),
                    title = manga.name,
                    ruTitle = manga.russian,
                    imageUrl = "${BuildConfig.SHIKI_BASE_URL}${manga.image.x96}",
                    score = score,
                    status = status.toDomain(),
                    progress = manga.chapters
                )
            }
        }
    }

    fun List<UserRate>.toDomain(): MediaTypeStats<OverviewStats> {
        val statsMap = this.groupBy { it.mediaType }
            .mapValues { (_, rates) ->
                OverviewStats(
                    shortStats = listOf(
                        ShortOverviewStat(
                            count = rates.size.toString(),
                            statType = OverviewStatType.TITLE
                        ),
                        ShortOverviewStat(
                            count = rates.map { it.score }.average().toString(),
                            statType = OverviewStatType.MEAN_SCORE
                        )
                    ),
                    scoreStatsTitles = rates
                        .filter { rate ->
                            rate.status == UserRateStatus.COMPLETED &&
                                rate.score > 0
                        }
                        .groupBy { it.score }
                        .map { (score, rates) -> Stat(type = score, value = rates.size.toFloat()) }
                        .sortedBy { it.type },
                    statusesStats = rates
                        .groupBy { it.status }
                        .map { (status, rates) -> Stat(type = status, value = rates.size.toFloat()) }
                        .sortedBy { UserRateStatus.entries.indexOf(it.type) }
                )
            }

        return MediaTypeStats(
            animeStats = statsMap[MediaType.ANIME],
            mangaStats = statsMap[MediaType.MANGA]
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