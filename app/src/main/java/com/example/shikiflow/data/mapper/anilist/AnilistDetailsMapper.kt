package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.MediaDetailsQuery
import com.example.graphql.anilist.fragment.MediaBrowse
import com.example.shikiflow.data.mapper.anilist.AnilistCharacterMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistRateMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toDomain
import com.example.shikiflow.data.mapper.common.DateMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaOriginMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toDomain
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.RelatedMediaMapper.toDomain
import com.example.shikiflow.data.mapper.common.StudioMapper.toDomain
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaOrigin
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.Stat
import kotlin.time.Instant

object AnilistDetailsMapper {
    fun MediaDetailsQuery.Media.toDomain(): MediaDetails {
        return MediaDetails(
            id = this.id,
            malId = this.idMal ?: 0,
            mediaType = this.type?.toDomain() ?: MediaType.ANIME,
            title = this.title?.romaji ?: "",
            descriptionHtml = this.description,
            native = this.title?.native ?: "",
            synonyms = this.synonyms?.mapNotNull { it } ?: emptyList(),
            coverImageUrl = this.coverImage?.extraLarge ?: "",
            score = (this.averageScore ?: 0) / 10f,
            totalCount = this.episodes ?: this.chapters,
            currentProgress = this.nextAiringEpisode?.aLAiringEpisodeShort?.episode?.minus(1),
            volumes = this.volumes,
            format = this.format?.toDomain() ?: MediaFormat.UNKNOWN,
            status = this.status?.toDomain() ?: MediaStatus.UNKNOWN,
            genres = this.genres?.mapNotNull { it } ?: emptyList(),
            characters = PaginatedList(
                hasNextPage = this.characters?.pageInfo?.hasNextPage == true,
                entries = this.characters?.nodes?.mapNotNull { it?.aLCharacterShort?.toDomain() }.orEmpty()
            ),
            airedOn = this.startDate?.date?.toDomain(),
            releasedOn = this.endDate?.date?.toDomain(),
            nextEpisodeAt = this.nextAiringEpisode?.let {
                Instant.fromEpochSeconds(it.aLAiringEpisodeShort.airingAt.toLong())
            },
            origin = this.source?.toDomain() ?: MediaOrigin.UNKNOWN,
            userRate = this.mediaListEntry?.aLRateEntry?.toDomain(),
            studios = this.studios?.nodes?.mapNotNull { it?.aLStudioShort?.toDomain() } ?: emptyList(),
            staffList = this.staff?.edges?.mapNotNull { it?.aLStaffEdgeShort?.toDomain() } ?: emptyList(),
            durationMins = this.duration,
            relatedMedia = this.relations?.edges?.mapNotNull { it?.aLRelatedMediaShort?.toDomain() } ?: emptyList(),
            scoreStats = stats?.aLMediaStats?.scoreDistribution?.mapNotNull { scoreItem ->
                Stat<Int>(
                    type = scoreItem?.score ?: 0,
                    value = scoreItem?.amount?.toFloat() ?: 0f
                )
            }.orEmpty(),
            statusesStats = stats?.aLMediaStats?.statusDistribution?.mapNotNull { statusItem ->
                Stat<UserRateStatus>(
                    type = statusItem?.status?.toDomain() ?: UserRateStatus.UNKNOWN,
                    value = statusItem?.amount?.toFloat() ?: 0f
                )
            }
                ?.filter { it.value != 0f }
                .orEmpty()
        )
    }

    fun MediaBrowse.toBrowse(mediaType: MediaType): Browse {
        return when(mediaType) {
            MediaType.ANIME -> {
                Browse.Anime(
                    id = id,
                    title = title?.romaji ?: "",
                    posterUrl = coverImage?.extraLarge,
                    score = averageScore?.div(10.0f),
                    nextEpisodeAt = nextAiringEpisode?.let {
                        Instant.fromEpochSeconds(it.airingAt.toLong())
                    },
                    mediaType = mediaType,
                    mediaFormat = format?.toDomain() ?: MediaFormat.UNKNOWN,
                    userRateStatus = mediaListEntry?.status?.toDomain(),
                    episodesAired = nextAiringEpisode?.episode?.let { it - 1 } ?: episodes,
                    episodes = episodes,
                    studios = this.studios?.nodes?.mapNotNull { it?.aLStudioShort?.name }
                        ?: emptyList(),
                    genres = this.genres?.mapNotNull { it } ?: emptyList()
                )
            }
            MediaType.MANGA -> {
                Browse.Manga(
                    id = id,
                    title = title?.romaji ?: "",
                    posterUrl = coverImage?.extraLarge,
                    score = averageScore?.div(10.0f),
                    nextEpisodeAt = nextAiringEpisode?.let {
                        Instant.fromEpochSeconds(it.airingAt.toLong())
                    },
                    mediaType = mediaType,
                    mediaFormat = format?.toDomain() ?: MediaFormat.UNKNOWN,
                    userRateStatus = mediaListEntry?.status?.toDomain()
                )
            }
        }
    }
}