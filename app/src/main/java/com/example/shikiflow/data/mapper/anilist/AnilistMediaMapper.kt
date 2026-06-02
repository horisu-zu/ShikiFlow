package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.MediaDetailsQuery
import com.example.graphql.anilist.fragment.ALAiringAnime
import com.example.graphql.anilist.fragment.ALAiringAnimeShort
import com.example.graphql.anilist.fragment.MediaBrowse
import com.example.graphql.anilist.fragment.MediaFollowingShort
import com.example.shikiflow.data.mapper.anilist.AnilistCharacterMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistReviewMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomain
import com.example.shikiflow.data.mapper.common.DateMapper.toDomain
import com.example.shikiflow.data.mapper.common.GenreMapper
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaOriginMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTitleMapper.toDomainTitle
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toDomain
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.RelatedMediaMapper.toDomain
import com.example.shikiflow.data.mapper.common.ScoreFormatMapper.toDomainFormat
import com.example.shikiflow.data.mapper.common.StudioMapper.toStudioShort
import com.example.shikiflow.data.mapper.common.TagMapper
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.anime.AiringAnimeDataShort
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaFollowing
import com.example.shikiflow.domain.model.media_details.MediaOrigin
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.media_details.MediaTag
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.Stat
import com.example.shikiflow.utils.StringUtils.stripHtml
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

object AnilistMediaMapper {
    fun MediaDetailsQuery.Media.toDomain(mediaFollowings: PaginatedList<MediaFollowing>): MediaDetails {
        return MediaDetails(
            id = id,
            malId = idMal,
            mediaType = type?.toDomain() ?: MediaType.ANIME,
            title = title?.mediaTitle.toDomainTitle(),
            descriptionHtml = description?.stripHtml(),
            synonyms = synonyms?.mapNotNull { it } ?: emptyList(),
            coverImageUrl = coverImage?.extraLarge ?: coverImage?.large ?: "",
            bannerImageUrl = bannerImage,
            score = (averageScore ?: 0) / 10f,
            totalCount = episodes ?: chapters,
            currentProgress = nextAiringEpisode?.aLAiringEpisodeShort?.episode?.minus(1),
            volumes = volumes,
            format = format?.toDomain() ?: MediaFormat.UNKNOWN,
            status = status?.toDomain() ?: MediaStatus.UNKNOWN,
            genres = genres?.mapNotNull { genre ->
                genre?.let {
                    GenreMapper.fromString(genre)
                }
            } ?: emptyList(),
            tags = tags?.mapNotNull { tag ->
                tag?.let {
                    TagMapper.fromString(tag.name)?.let { tagEnum ->
                        MediaTag(
                            tag = tagEnum,
                            isSpoiler = tag.isMediaSpoiler == true
                        )
                    }
                }
            } ?: emptyList(),
            characters = PaginatedList(
                hasNextPage = characters?.pageInfo?.hasNextPage == true,
                entries = characters?.nodes?.mapNotNull { it?.aLCharacterShort?.toDomain() }.orEmpty()
            ),
            airedOn = startDate?.date?.toDomain(),
            releasedOn = endDate?.date?.toDomain(),
            nextEpisodeAt = nextAiringEpisode?.let {
                Instant.fromEpochSeconds(it.aLAiringEpisodeShort.airingAt.toLong())
            },
            origin = source?.toDomain() ?: MediaOrigin.UNKNOWN,
            studios = studios?.nodes?.mapNotNull { it?.aLStudioShort?.toStudioShort() } ?: emptyList(),
            staffList = staff?.edges?.mapNotNull { staffEdge ->
                staffEdge?.aLStaffEdgeShort?.toDomain()
            }
                ?.groupBy { it.id }
                ?.mapValues { (_, staffRoles) ->
                    val staffData = staffRoles.first()

                    StaffShort(
                        id = staffData.id,
                        fullName = staffData.fullName,
                        imageUrl = staffData.imageUrl,
                        roles = staffRoles.flatMap { it.roles },
                    )
                }
                ?.values
                ?.toList() ?: emptyList(),
            durationMins = duration,
            relatedMedia = relations?.edges?.mapNotNull { it?.aLRelatedMediaShort?.toDomain() } ?: emptyList(),
            reviews = PaginatedList(
                hasNextPage = reviews?.pageInfo?.hasNextPage == true,
                entries = reviews?.nodes?.mapNotNull { it?.aLReviewShort?.toDomain() }.orEmpty()
            ),
            scoreStats = stats?.aLMediaStats?.scoreDistribution?.mapNotNull { scoreItem ->
                Stat(
                    type = scoreItem?.score ?: 0,
                    value = scoreItem?.amount?.toFloat() ?: 0f
                )
            }.orEmpty(),
            statusesStats = stats?.aLMediaStats?.statusDistribution?.mapNotNull { statusItem ->
                Stat(
                    type = statusItem?.status?.toDomain() ?: UserRateStatus.UNKNOWN,
                    value = statusItem?.amount?.toFloat() ?: 0f
                )
            }
                ?.filter { it.value != 0f }
                .orEmpty(),
            isFavorite = isFavourite,
            mediaFollowings = mediaFollowings
        )
    }

    fun MediaFollowingShort.toDomain(): MediaFollowing? {
        return user?.aLMediaFollowingUser?.let { user ->
             MediaFollowing(
                 user = user.toDomain(),
                 mediaType = media?.type?.toDomain() ?: MediaType.ANIME,
                 status = status?.toDomain() ?: UserRateStatus.UNKNOWN,
                 progress = progress,
                 progressVolumes = progressVolumes,
                 score = score?.toFloat(),
                 scoreFormat = user.mediaListOptions?.scoreFormat?.toDomainFormat() ?: ScoreFormat.POINT_10
            )
        }
    }

    fun MediaBrowse.toBrowse(mediaType: MediaType): BrowseMedia {
        return when(mediaType) {
            MediaType.ANIME -> {
                BrowseMedia.Anime(
                    id = id,
                    title = title?.mediaTitle.toDomainTitle(),
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
                    studios = studios?.nodes?.mapNotNull { it?.aLStudioShort?.name }
                        ?: emptyList(),
                    genres = genres?.mapNotNull { genre ->
                        genre?.toDomainTitle(null, null, null)
                    } ?: emptyList()
                )
            }
            MediaType.MANGA -> {
                BrowseMedia.Manga(
                    id = id,
                    title = title?.mediaTitle.toDomainTitle(),
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

    fun ALAiringAnimeShort.toDomain(): AiringAnime? {
        return media?.aLAiringAnime?.let { airingAnime ->
            AiringAnime(
                data = airingAnime.toAiringData(),
                episode = episode,
                timeUntilAiring = timeUntilAiring.toLong().seconds,
                airingAt = Instant.fromEpochSeconds(airingAt.toLong())
            )
        }
    }

    fun ALAiringAnime.toAiringData(): AiringAnimeDataShort {
        return AiringAnimeDataShort(
            id = id,
            title = title?.mediaTitle.toDomainTitle(),
            mediaType = type?.toDomain() ?: MediaType.ANIME,
            coverImageUrl = coverImage?.large ?: "",
            totalEpisodes = episodes,
            userRateStatus = mediaListEntry?.status?.toDomain(),
            duration = duration?.minutes,
            isAdult = isAdult ?: false
        )
    }
}