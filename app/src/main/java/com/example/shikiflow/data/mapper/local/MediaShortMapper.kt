package com.example.shikiflow.data.mapper.local

import com.example.graphql.anilist.fragment.MediaShort
import com.example.graphql.shikimori.fragment.AnimeShort
import com.example.graphql.shikimori.fragment.MangaShort
import com.example.shikiflow.data.local.entity.PosterEntity.Companion.toDomain
import com.example.shikiflow.data.local.entity.PosterEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.ReleaseDateEntity.Companion.toDomain
import com.example.shikiflow.data.local.entity.ReleaseDateEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.mediatrack.MediaShortEntity
import com.example.shikiflow.data.mapper.common.DateMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toDomain
import com.example.shikiflow.data.mapper.common.PosterMapper.toDomain
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.track.Poster
import com.example.shikiflow.domain.model.track.media.MediaShortData
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlin.time.Instant

object MediaShortMapper {
    fun MediaDetails.toShortData(): MediaShortData {
        return MediaShortData(
            id = id,
            malId = malId,
            name = title,
            synonyms = listOfNotNull(native),
            mediaType = mediaType,
            kind = format,
            score = score,
            status = status,
            totalCount = totalCount ?: 0,
            volumes = volumes,
            currentProgress = currentProgress ?: 0,
            nextEpisodeAt = nextEpisodeAt,
            duration = durationMins,
            airedOn = airedOn,
            releasedOn = releasedOn,
            studios = studios?.map { it.name } ?: emptyList(),
            genres = genres,
            poster = Poster(originalUrl = coverImageUrl)
        )
    }

    fun MediaShortEntity.toDomain(): MediaShortData {
        return MediaShortData(
            id = id,
            malId = malId,
            name = name,
            synonyms = synonyms,
            mediaType = mediaType,
            kind = kind,
            score = score,
            status = status,
            totalCount = progress,
            volumes = volumes,
            currentProgress = episodesAired,
            nextEpisodeAt = nextEpisodeAt,
            duration = duration,
            airedOn = airedOn?.toDomain(),
            releasedOn = releasedOn?.toDomain(),
            poster = poster?.toDomain(),
            genres = genres,
            studios = studios
        )
    }

    fun MediaShortData.toEntity(): MediaShortEntity {
        return MediaShortEntity(
            id = id,
            malId = malId,
            name = name,
            synonyms = synonyms,
            mediaType = mediaType,
            kind = kind,
            score = score,
            status = status,
            progress = totalCount,
            volumes = volumes,
            episodesAired = currentProgress,
            nextEpisodeAt = nextEpisodeAt,
            duration = duration,
            airedOn = airedOn?.toDto(),
            releasedOn = releasedOn?.toDto(),
            poster = poster?.toDto(),
            genres = genres,
            studios = studios
        )
    }

    /** In Shikimori API, Anime/Manga ID is the same as MAL ID,
     *  even though there's separate MAL ID field I can't use because of the query complexity limit
     */
    fun AnimeShort.toShortData(): MediaShortData {
        return MediaShortData(
            id = id.toInt(),
            malId = id.toInt(),
            name = name,
            synonyms = synonyms,
            mediaType = MediaType.ANIME,
            kind = kind?.toDomain(),
            score = score?.toFloat(),
            status = status?.toDomain(),
            totalCount = episodes,
            volumes = null,
            currentProgress = episodesAired,
            nextEpisodeAt = nextEpisodeAt?.let { Instant.parse(it.toString()) },
            duration = duration,
            airedOn = airedOn?.dateShort?.toDomain(),
            releasedOn = releasedOn?.dateShort?.toDomain(),
            studios = studios.map { name },
            genres = genres?.map { name } ?: emptyList(),
            poster = poster?.posterShort?.toDomain()
        )
    }

    fun MangaShort.toShortData(): MediaShortData {
        return MediaShortData(
            id = id.toInt(),
            malId = id.toInt(),
            name = name,
            synonyms = synonyms,
            mediaType = MediaType.MANGA,
            kind = kind?.toDomain(),
            score = score?.toFloat(),
            status = status?.toDomain(),
            totalCount = chapters,
            volumes = volumes,
            currentProgress = chapters,
            nextEpisodeAt = null,
            duration = null,
            airedOn = airedOn?.dateShort?.toDomain(),
            releasedOn = releasedOn?.dateShort?.toDomain(),
            studios = null,
            genres = genres?.map { name } ?: emptyList(),
            poster = poster?.posterShort?.toDomain()
        )
    }

    fun MediaShort.toShortData() = MediaShortData(
        id = id,
        malId = idMal,
        name = title?.romaji ?: "No Title",
        synonyms = buildList {
            title?.english?.let { add(it) }
            synonyms?.mapNotNull { synonym ->
                synonym?.let { add(it) }
            }
        },
        mediaType = type?.toDomain() ?: MediaType.ANIME,
        kind = format?.toDomain(),
        score = averageScore?.div(10.0f),
        status = status?.toDomain(),
        totalCount = episodes ?: chapters ?: 0,
        currentProgress = nextAiringEpisode?.episode?.minus(1),
        volumes = volumes,
        nextEpisodeAt =  nextAiringEpisode?.let { Instant.fromEpochMilliseconds(it.airingAt.toLong()) },
        duration = duration,
        airedOn = startDate?.date?.toDomain(),
        releasedOn = endDate?.date?.toDomain(),
        studios = studios?.nodes?.mapNotNull { it?.name } ?: emptyList(),
        genres = genres?.mapNotNull { it } ?: emptyList(),
        poster = coverImage?.mediaCoverShort?.toDomain()
    )
}