package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.type.UserRateStatusEnum
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.ShikiUserRateResponse
import com.example.shikiflow.data.datasource.dto.media.ShikiShortUserRate
import com.example.shikiflow.data.datasource.dto.ShikiShortUserRateResponse
import com.example.shikiflow.data.datasource.dto.ShikiTargetType.Companion.toMediaType
import com.example.shikiflow.data.mapper.common.MediaTitleMapper.toDomainTitle
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
            id = id.toInt(),
            status = UserRateStatusEnum.valueOf(status).toDomain(),
            score = score,
            mediaType = shikiTargetType.toMediaType()
        )
    }

    fun ShikiUserRateResponse.toDomain(mediaType: MediaType): UserMediaRate {
        return UserMediaRate(
            rateId = id.toInt(),
            mediaId = targetId.toInt(),
            rateStatus = UserRateStatusEnum.valueOf(status).toDomain(),
            progress = when(mediaType) {
                MediaType.ANIME -> episodes
                MediaType.MANGA -> chapters
            } ?: 0,
            progressVolumes = volumes ?: 0,
            repeat = rewatches,
            textNotes = text,
            score = score.times(10),
            createdAt = Instant.parse(createdAt),
            updatedAt = Instant.parse(updatedAt)
        )
    }

    fun ShikiShortUserRate.toDomain(): ShortUserMediaRate {
        return when(this) {
            is ShikiShortUserRate.ShikiShortAnimeRate -> {
                ShortUserMediaRate(
                    id = anime.id.toInt(),
                    title = anime.name.toDomainTitle(anime.name, anime.russian, null),
                    synonyms = emptyList(),
                    imageUrl = "${BuildConfig.SHIKI_BASE_URL}${anime.image.x96}",
                    score = score * 10,
                    status = status.toDomain(),
                    progress = anime.episodes,
                    genres = emptyList(),
                    tags = emptyList()
                )
            }
            is ShikiShortUserRate.ShikiShortMangaRate -> {
                ShortUserMediaRate(
                    id = manga.id.toInt(),
                    title = manga.name.toDomainTitle(manga.name, manga.russian, null),
                    synonyms = emptyList(),
                    imageUrl = "${BuildConfig.SHIKI_BASE_URL}${manga.image.x96}",
                    score = score * 10,
                    status = status.toDomain(),
                    progress = manga.chapters,
                    genres = emptyList(),
                    tags = emptyList()
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
}