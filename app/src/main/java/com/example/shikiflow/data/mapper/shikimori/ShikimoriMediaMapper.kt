package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.AnimeBrowseQuery
import com.example.graphql.shikimori.AnimeDetailsQuery
import com.example.graphql.shikimori.MangaBrowseQuery
import com.example.graphql.shikimori.MangaDetailsQuery
import com.example.graphql.shikimori.type.AnimeKindEnum
import com.example.graphql.shikimori.type.MangaKindEnum
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.CalendarAnime
import com.example.shikiflow.data.datasource.dto.ShikiAnime
import com.example.shikiflow.data.datasource.dto.ShikiManga
import com.example.shikiflow.data.mapper.common.DateMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaOriginMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.RatingMapper.toDomain
import com.example.shikiflow.data.mapper.common.RelatedMediaMapper.toDomain
import com.example.shikiflow.data.mapper.common.StudioMapper.toStudioShort
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toDomain
import com.example.shikiflow.data.mapper.shikimori.ShikimoriRateMapper.toDomain
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.toDomain
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ShortMedia
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaOrigin
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.Stat
import com.example.shikiflow.utils.DateUtils.isInCurrentWeek
import com.example.shikiflow.utils.DateUtils.timeDifference
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

object ShikimoriMediaMapper {
    fun AnimeDetailsQuery.Anime.toDomain(): MediaDetails {
        return MediaDetails(
            id = id.toInt(),
            malId = id.toInt(),
            mediaType = MediaType.ANIME,
            title = name,
            descriptionHtml = descriptionHtml ?: "",
            native = japanese ?: "",
            synonyms = synonyms,
            coverImageUrl = poster?.originalUrl ?: "",
            score = score?.toFloat() ?: 0.0f,
            totalCount = episodes,
            currentProgress = episodesAired,
            volumes = null,
            format = kind?.toDomain() ?: MediaFormat.UNKNOWN,
            status = status?.toDomain() ?: MediaStatus.UNKNOWN,
            ageRating = rating?.toDomain(),
            genres = genres?.map { it.name } ?: emptyList(),
            characters = PaginatedList(
                hasNextPage = false,
                entries = characterRoles?.map { it.character.characterShort.toDomain() }.orEmpty()
            ),
            airedOn = airedOn?.dateShort?.toDomain(),
            releasedOn = releasedOn?.dateShort?.toDomain(),
            nextEpisodeAt = nextEpisodeAt?.let { Instant.parse(it.toString()) },
            origin = origin?.toDomain() ?: MediaOrigin.UNKNOWN,
            screenshots = screenshots.map { it.originalUrl },
            userRate = userRate?.userRateShort?.toDomain(mediaId = id.toInt(), MediaType.ANIME),
            studios = studios.map { it.toStudioShort() },
            durationMins = duration,
            relatedMedia = related?.map { it.relatedMediaShort.toDomain() } ?: emptyList(),
            scoreStats = scoresStats?.map { (score, count) ->
                Stat<Int>(
                    type = score,
                    value = count.toFloat()
                )
            }?.sortedBy { it.type }.orEmpty(),
            statusesStats = statusesStats?.map { (status, count) ->
                Stat<UserRateStatus>(
                    type = status.toDomain(),
                    value = count.toFloat()
                )
            }.orEmpty(),
            threadId = topic?.id?.toInt()
        )
    }

    fun MangaDetailsQuery.Manga.toDomain(): MediaDetails {
        return MediaDetails(
            id = id.toInt(),
            malId = id.toInt(),
            mediaType = MediaType.MANGA,
            title = name,
            descriptionHtml = descriptionHtml ?: "",
            native = japanese ?: "",
            synonyms = synonyms,
            coverImageUrl = poster?.originalUrl ?: "",
            score = score?.toFloat() ?: 0.0f,
            totalCount = chapters,
            volumes = volumes,
            format = kind?.toDomain() ?: MediaFormat.UNKNOWN,
            status = status?.toDomain() ?: MediaStatus.UNKNOWN,
            genres = genres?.map { it.name } ?: emptyList(),
            characters = PaginatedList(
                hasNextPage = false,
                entries = characterRoles?.map { it.character.characterShort.toDomain() }.orEmpty()
            ),
            airedOn = airedOn?.dateShort?.toDomain(),
            releasedOn = releasedOn?.dateShort?.toDomain(),
            userRate = userRate?.userRateShort?.toDomain(mediaId = id.toInt(), MediaType.MANGA),
            relatedMedia = related?.map { it.relatedMediaShort.toDomain() } ?: emptyList(),
            staffList = personRoles?.map { it.personRoleShort.toDomain() } ?: emptyList(),
            scoreStats = scoresStats?.map { (score, count) ->
                Stat<Int>(
                    type = score,
                    value = count.toFloat()
                )
            }?.sortedBy { it.type }.orEmpty(),
            statusesStats = statusesStats?.map { (status, count) ->
                Stat<UserRateStatus>(
                    type = status.toDomain(),
                    value = count.toFloat()
                )
            }.orEmpty(),
            threadId = topic?.id?.toInt()
        )
    }

    fun AnimeBrowseQuery.Anime.toBrowseAnime(): BrowseMedia.Anime {
        return BrowseMedia.Anime(
            id = this.id.toInt(),
            title = this.name,
            posterUrl = this.poster?.posterShort?.mainUrl,
            score = this.score?.toFloat(),
            mediaFormat = this.kind?.toDomain() ?: MediaFormat.UNKNOWN,
            nextEpisodeAt = this.nextEpisodeAt?.let { Instant.parse(nextEpisodeAt.toString()) },
            userRateStatus = this.userRate?.animeUserRate?.status?.toDomain(),
            episodesAired = this.episodesAired,
            episodes = this.episodes,
            studios = this.studios.map { it.name },
            genres = this.genres?.map { it.name } ?: emptyList()
        )
    }

    fun MangaBrowseQuery.Manga.toBrowseManga(): BrowseMedia.Manga {
        return BrowseMedia.Manga(
            id = this.id.toInt(),
            title = name,
            posterUrl = this.poster?.posterShort?.originalUrl,
            score = this.score?.toFloat(),
            mediaType = MediaType.MANGA,
            mediaFormat = this.kind?.toDomain() ?: MediaFormat.UNKNOWN,
            userRateStatus = this.userRate?.mangaUserRate?.status?.toDomain()
        )
    }

    fun ShikiManga.toBrowseManga(): BrowseMedia.Manga {
        return BrowseMedia.Manga(
            id = this.id ?: 0,
            title = this.name ?: "Unknown",
            posterUrl = "${BuildConfig.SHIKI_BASE_URL}${this.image?.original}",
            score = this.score?.toFloat(),
            mediaFormat = MangaKindEnum.valueOf(this.kind ?: "UNKNOWN__").toDomain(),
            userRateStatus = null
        )
    }

    fun ShikiAnime.toBrowseAnime(): BrowseMedia.Anime {
        return BrowseMedia.Anime(
            id = this.id ?: 0,
            title = this.name ?: "Unknown",
            posterUrl = "${BuildConfig.SHIKI_BASE_URL}${this.image?.original}",
            score = this.score?.toFloat(),
            mediaFormat = AnimeKindEnum.valueOf(this.kind ?: "UNKNOWN__").toDomain(),
            episodesAired = this.episodesAired ?: 0,
            episodes = this.episodes ?: 0,
            userRateStatus = null
        )
    }

    fun AnimeBrowseQuery.Anime.toAiringAnime(): AiringAnime {
        val airedOnDate = airedOn?.dateShort?.toDomain()?.date

        val nextEpInstant = nextEpisodeAt?.let { Instant.parse(nextEpisodeAt.toString()) }
        val airedThisWeek = airedOnDate?.isInCurrentWeek() == true ||
            nextEpInstant?.isInCurrentWeek() != true

        val episodeInstant = nextEpInstant?.let {
            if(airedThisWeek) nextEpInstant.minus(7.days) else nextEpInstant
        } ?: airedOnDate

        return AiringAnime(
            data = ShortMedia(
                id = id.toInt(),
                title = name,
                mediaType = MediaType.ANIME,
                coverImageUrl = poster?.posterShort?.originalUrl ?: "",
                userRateStatus = userRate?.animeUserRate?.status?.toDomain()
            ),
            episode = if(airedThisWeek) episodesAired else episodesAired + 1,
            timeUntilAiring = episodeInstant?.timeDifference(),
            airingAt = episodeInstant,
            releasedOn = releasedOn?.dateShort?.toDomain()?.date
        )
    }

    fun CalendarAnime.toAiringAnime(): AiringAnime {
        return AiringAnime(
            data = ShortMedia(
                id = shikiAnime.id ?: 0,
                title = shikiAnime.name ?: "",
                mediaType = MediaType.ANIME,
                coverImageUrl = BuildConfig.SHIKI_BASE_URL + shikiAnime.image?.original,
                userRateStatus = null
            ),
            episode = nextEpisode,
            airingAt = Instant.parse(nextEpisodeAt),
            timeUntilAiring = Instant.parse(nextEpisodeAt).timeDifference()
        )
    }
}