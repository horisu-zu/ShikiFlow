package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.AnimeBrowseQuery
import com.example.graphql.shikimori.AnimeDetailsQuery
import com.example.graphql.shikimori.MangaBrowseQuery
import com.example.graphql.shikimori.MangaDetailsQuery
import com.example.graphql.shikimori.type.AnimeKindEnum
import com.example.graphql.shikimori.type.GenreKindEnum
import com.example.graphql.shikimori.type.MangaKindEnum
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.ShikiAnime
import com.example.shikiflow.data.datasource.dto.ShikiManga
import com.example.shikiflow.data.mapper.common.DateMapper.toDomain
import com.example.shikiflow.data.mapper.common.GenreMapper
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaOriginMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTitleMapper.toDomainTitle
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.RatingMapper.toDomain
import com.example.shikiflow.data.mapper.common.RelatedMediaMapper.toDomain
import com.example.shikiflow.data.mapper.common.StudioMapper.toStudioShort
import com.example.shikiflow.data.mapper.common.TagMapper
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toDomain
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.sortByRole
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.toDomain
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.anime.AiringAnimeDataShort
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaOrigin
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.media_details.MediaTag
import com.example.shikiflow.domain.model.media_details.MediaTitle
import com.example.shikiflow.domain.model.track.Date
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.Stat
import com.example.shikiflow.utils.DateUtils.isInCurrentWeek
import com.example.shikiflow.utils.DateUtils.timeDifference
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

object ShikimoriMediaMapper {
    fun AnimeDetailsQuery.Anime.toDomain(): MediaDetails {
        return MediaDetails(
            id = id.toInt(),
            malId = id.toInt(),
            mediaType = MediaType.ANIME,
            title = name.toDomainTitle(english, russian, japanese),
            descriptionHtml = descriptionHtml ?: "",
            synonyms = synonyms,
            coverImageUrl = poster?.originalUrl ?: "",
            bannerImageUrl = null,
            score = score?.toFloat() ?: 0.0f,
            totalCount = episodes,
            currentProgress = episodesAired,
            volumes = null,
            format = kind?.toDomain() ?: MediaFormat.UNKNOWN,
            status = status?.toDomain() ?: MediaStatus.UNKNOWN,
            ageRating = rating?.toDomain(),
            genres = genres?.mapNotNull { genre ->
                GenreMapper.fromString(genre.genreShort.name)
            } ?: emptyList(),
            tags = genres
                ?.filter { genre ->
                    genre.genreShort.kind != GenreKindEnum.genre
                }?.mapNotNull { genre ->
                    TagMapper.fromString(genre.genreShort.name)?.let { tag ->
                        MediaTag(tag = tag)
                    }
                } ?: emptyList(),
            characters = PaginatedList(
                hasNextPage = false,
                entries = characterRoles?.map { it.character.characterShort.toDomain() }.orEmpty()
            ),
            airedOn = airedOn?.dateShort?.toDomain(),
            releasedOn = releasedOn?.dateShort?.toDomain(),
            nextEpisodeAt = nextEpisodeAt?.let { Instant.parse(it.toString()) },
            origin = origin?.toDomain() ?: MediaOrigin.UNKNOWN,
            screenshots = screenshots.map { it.originalUrl },
            studios = studios.map { it.toStudioShort() },
            staffList = personRoles?.map { it.personRoleShort.toDomain() }
                ?.sortByRole() ?: emptyList(),
            durationMins = duration,
            relatedMedia = related?.map { it.relatedMediaShort.toDomain() } ?: emptyList(),
            scoreStats = scoresStats?.map { (score, count) ->
                Stat(
                    type = score,
                    value = count.toFloat()
                )
            }?.sortedBy { it.type }.orEmpty(),
            statusesStats = statusesStats?.map { (status, count) ->
                Stat(
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
            title = name.toDomainTitle(english, russian, japanese),
            descriptionHtml = descriptionHtml ?: "",
            synonyms = emptyList(),
            coverImageUrl = poster?.originalUrl ?: "",
            bannerImageUrl = null,
            score = score?.toFloat() ?: 0.0f,
            totalCount = chapters,
            volumes = volumes,
            format = kind?.toDomain() ?: MediaFormat.UNKNOWN,
            status = status?.toDomain() ?: MediaStatus.UNKNOWN,
            genres = genres?.mapNotNull { genre ->
                GenreMapper.fromString(genre.genreShort.name)
            } ?: emptyList(),
            tags = genres
                ?.filter { genre ->
                    genre.genreShort.kind != GenreKindEnum.genre
                }?.mapNotNull { genre ->
                    TagMapper.fromString(genre.genreShort.name)?.let { tag ->
                        MediaTag(tag = tag)
                    }
                } ?: emptyList(),
            characters = PaginatedList(
                hasNextPage = false,
                entries = characterRoles?.map { it.character.toDomain() }.orEmpty()
            ),
            airedOn = airedOn?.let { airedOn ->
                airedOn.year?.let {
                    Date(
                        year = airedOn.year,
                        month = airedOn.month,
                        day = airedOn.day,
                        date = airedOn.date?.let { LocalDate.parse(it.toString())
                            .atStartOfDayIn(TimeZone.currentSystemDefault()) }
                    )
                }
            },
            releasedOn = releasedOn?.let { releasedOn ->
                releasedOn.year?.let {
                    Date(
                        year = releasedOn.year,
                        month = releasedOn.month,
                        day = releasedOn.day,
                        date = releasedOn.date?.let { LocalDate.parse(it.toString())
                            .atStartOfDayIn(TimeZone.currentSystemDefault()) }
                    )
                }
            },
            relatedMedia = related?.map { it.relatedMediaShort.toDomain() } ?: emptyList(),
            staffList = personRoles?.map { it.personRoleShort.toDomain() }
                ?.sortByRole() ?: emptyList(),
            scoreStats = scoresStats?.map { (score, count) ->
                Stat(
                    type = score,
                    value = count.toFloat()
                )
            }?.sortedBy { it.type }.orEmpty(),
            statusesStats = statusesStats?.map { (status, count) ->
                Stat(
                    type = status.toDomain(),
                    value = count.toFloat()
                )
            }.orEmpty(),
            threadId = topic?.id?.toInt()
        )
    }

    fun AnimeBrowseQuery.Anime.toBrowseAnime(): BrowseMedia.Anime {
        return BrowseMedia.Anime(
            id = id.toInt(),
            title = name.toDomainTitle(english, russian, japanese),
            posterUrl = poster?.posterShort?.mainUrl,
            score = score?.toFloat(),
            mediaFormat = kind?.toDomain() ?: MediaFormat.UNKNOWN,
            nextEpisodeAt = nextEpisodeAt?.let { Instant.parse(nextEpisodeAt.toString()) },
            userRateStatus = userRate?.animeUserRate?.status?.toDomain(),
            episodesAired = episodesAired,
            episodes = episodes,
            studios = studios.map { it.name },
            genres = genres?.map { genre ->
                MediaTitle(
                    romaji = genre.name,
                    english = null,
                    russian = genre.russian,
                    native = null
                )
            } ?: emptyList()
        )
    }

    fun MangaBrowseQuery.Manga.toBrowseManga(): BrowseMedia.Manga {
        return BrowseMedia.Manga(
            id = id.toInt(),
            title = name.toDomainTitle(english, russian, japanese),
            posterUrl = poster?.posterShort?.originalUrl,
            score = score?.toFloat(),
            mediaType = MediaType.MANGA,
            mediaFormat = kind?.toDomain() ?: MediaFormat.UNKNOWN,
            userRateStatus = userRate?.mangaUserRate?.status?.toDomain()
        )
    }

    fun ShikiManga.toBrowseManga(): BrowseMedia.Manga {
        return BrowseMedia.Manga(
            id = id ?: 0,
            title = (name ?: "").toDomainTitle(null, russian, null),
            posterUrl = "${BuildConfig.SHIKI_BASE_URL}${image?.original}",
            score = score?.toFloat(),
            mediaFormat = MangaKindEnum.valueOf(kind ?: "UNKNOWN__").toDomain(),
            userRateStatus = null
        )
    }

    fun ShikiAnime.toBrowseAnime(): BrowseMedia.Anime {
        return BrowseMedia.Anime(
            id = id ?: 0,
            title = (name ?: "").toDomainTitle(null, russian, null),
            posterUrl = "${BuildConfig.SHIKI_BASE_URL}${image?.original}",
            score = score?.toFloat(),
            mediaFormat = AnimeKindEnum.valueOf(kind ?: "UNKNOWN__").toDomain(),
            episodesAired = episodesAired ?: 0,
            episodes = episodes ?: 0,
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
            data = AiringAnimeDataShort(
                id = id.toInt(),
                title = name.toDomainTitle(english, russian, japanese),
                mediaType = MediaType.ANIME,
                coverImageUrl = poster?.posterShort?.originalUrl ?: "",
                userRateStatus = userRate?.animeUserRate?.status?.toDomain(),
                totalEpisodes = if(episodes != 0) episodes else null,
                duration = duration?.minutes
            ),
            episode = if(airedThisWeek) episodesAired else episodesAired + 1,
            timeUntilAiring = episodeInstant?.timeDifference(),
            airingAt = episodeInstant,
            releasedOn = releasedOn?.dateShort?.toDomain()?.date
        )
    }

    /*fun CalendarAnime.toAiringAnime(): AiringAnime {
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
    }*/
}