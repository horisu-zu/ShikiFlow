package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.UserStatsCategoriesQuery
import com.example.graphql.anilist.fragment.ALFavoriteCharacterShort
import com.example.graphql.anilist.fragment.ALFavoriteMediaShort
import com.example.graphql.anilist.fragment.ALFavoriteStaffShort
import com.example.graphql.anilist.fragment.ALFavoriteStudioShort
import com.example.graphql.anilist.fragment.ALListActivity
import com.example.graphql.anilist.fragment.ALMessageActivity
import com.example.graphql.anilist.fragment.ALTextActivity
import com.example.graphql.anilist.fragment.ALUserGenres
import com.example.graphql.anilist.fragment.ALUserListStats
import com.example.graphql.anilist.fragment.ALUserShort
import com.example.graphql.anilist.fragment.ALUserStaff
import com.example.graphql.anilist.fragment.ALUserStudios
import com.example.graphql.anilist.fragment.ALUserTags
import com.example.graphql.anilist.fragment.ALUserVoiceActors
import com.example.graphql.anilist.type.MediaListStatus
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toDomain
import com.example.shikiflow.data.mapper.common.CountryOfOriginMapper.toCountryOfOrigin
import com.example.shikiflow.data.mapper.common.DateMapper.minutesToDays
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toDomain
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.StudioMapper.toDomain
import com.example.shikiflow.domain.model.media_details.CountryOfOrigin
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.domain.model.user.stats.OverviewStatType
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.user.stats.ShortOverviewStat
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.stats.Stat
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.ListActivity
import com.example.shikiflow.domain.model.user.MessageActivity
import com.example.shikiflow.domain.model.user.TextActivity
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.stats.StudioStat
import kotlin.time.Instant

object AnilistUserMapper {
    fun ALUserShort.toDomain(): User {
        return User(
            id = this.id,
            nickname = this.name,
            avatarUrl = this.avatar?.large ?: "",
            profileBannerUrl = this.bannerImage
        )
    }

    fun ALListActivity.toDomain(): ListActivity {
        return ListActivity(
            id = id,
            mediaId = media?.id ?: 0,
            mediaType = media?.type?.toDomain(),
            title = media?.title?.romaji ?: "",
            coverImage = media?.coverImage?.extraLarge ?: "",
            description = buildString {
                status?.let { status ->
                    append(status.replaceFirstChar { it.uppercase() })
                }
                progress?.let { progress ->
                    append(" $progress")
                }
            },
            createdAt = Instant.fromEpochSeconds(createdAt.toLong())
        )
    }

    fun ALTextActivity.toDomain(): TextActivity {
        return TextActivity(
            id = id,
            text = text ?: "",
            user = user?.aLUserShort?.toDomain() ?: User(),
            createdAt = Instant.fromEpochSeconds(createdAt.toLong())
        )
    }

    fun ALMessageActivity.toDomain(): MessageActivity {
        return MessageActivity(
            id = id,
            text = message ?: "",
            messenger = messenger?.aLUserShort?.toDomain() ?: User(),
            recipient = recipient?.aLUserShort?.toDomain() ?: User(),
            createdAt = Instant.fromEpochSeconds(createdAt.toLong())
        )
    }

    fun ALFavoriteMediaShort.toUserFavorite(favoriteCategory: FavoriteCategory) = UserFavorite(
        id = id,
        name = title?.romaji ?: "",
        imageUrl = coverImage?.extraLarge ?: "",
        favoriteCategory = favoriteCategory
    )

    fun ALFavoriteCharacterShort.toUserFavorite() = UserFavorite(
        id = id,
        name = name?.full ?: "",
        imageUrl = image?.large ?: "",
        favoriteCategory = FavoriteCategory.CHARACTER
    )

    fun ALFavoriteStaffShort.toUserFavorite() = UserFavorite(
        id = id,
        name = name?.full ?: "",
        imageUrl = image?.large ?: "",
        favoriteCategory = FavoriteCategory.STAFF
    )

    fun ALFavoriteStudioShort.toUserFavorite() = UserFavorite(
        id = id,
        name = name,
        favoriteCategory = FavoriteCategory.STUDIO
    )

    fun UserStatsCategoriesQuery.Data.toDomain(): UserStatsCategories {
        val mediaTypes = buildList {
            User?.statistics?.anime?.count?.let { userCount ->
                if(userCount > 0) {
                    add(MediaType.ANIME)
                }
            }
            User?.statistics?.manga?.count?.let { userCount ->
                if(userCount > 0) {
                    add(MediaType.MANGA)
                }
            }
        }

        val favoriteCategories = mapOf(
            FavoriteCategory.ANIME to (User?.favourites?.anime?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.MANGA to (User?.favourites?.manga?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.CHARACTER to (User?.favourites?.characters?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.STAFF to (User?.favourites?.staff?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.STUDIO to (User?.favourites?.studios?.pageInfo?.aLPageInfoShort?.total ?: 0)
        )
            .filter { it.value > 0 }
            .keys
            .toList()

        val socialCategories = mapOf(
            SocialCategory.FOLLOWINGS to (followingsPage?.following?.size ?: 0),
            SocialCategory.FOLLOWERS to (followersPage?.followers?.size ?: 0),
            SocialCategory.THREADS to (threadsPage?.threads?.size ?: 0),
            SocialCategory.COMMENTS to (commentsPage?.threadComments?.size ?: 0)
        )
            .filter { it.value > 0 }
            .keys
            .toList()

        return UserStatsCategories(
            scoreMediaTypes = mediaTypes,
            favoriteCategories = favoriteCategories,
            socialCategories = socialCategories
        )
    }

    fun ALUserListStats.toOverviewStats(mediaType: MediaType): OverviewStats {
        return OverviewStats(
            shortStats = toShortStats(mediaType),
            scoreStatsTitles = toScoreStatsTitles(),
            scoreStatsTime = toScoreStatsTime(mediaType),
            statusesStats = toStatusesStats(),
            lengthStatsTitles = toLengthStatsTitles(),
            lengthStatsTime = toLengthStatsTime(mediaType),
            lengthStatsScore = toLengthStatsScore(),
            formatStats = toFormatStats(),
            countryStats = toCountryStats(),
            releaseYearStatsTitles = toReleaseYearStatsCount(),
            releaseYearStatsTime = toReleaseYearStatsTime(mediaType),
            releaseYearStatsScore = toReleaseYearStatsScore(),
            startYearStatsTitles = toStartYearStatsCount(),
            startYearStatsTime = toStartYearStatsTime(mediaType),
            startYearStatsScore = toStartYearStatsScore()
        )
    }

    fun ALUserListStats.toShortStats(mediaType: MediaType): List<ShortOverviewStat> {
        val plannedStatus = statuses?.find { it?.status == MediaListStatus.PLANNING }

        return listOf(
            ShortOverviewStat(count.toString(), statType = OverviewStatType.TITLE),
            ShortOverviewStat(
                count = when(mediaType) {
                    MediaType.ANIME -> episodesWatched.toString()
                    MediaType.MANGA -> chaptersRead.toString()
                },
                statType = OverviewStatType.EPISODE
            ),
            ShortOverviewStat(
                count = when(mediaType) {
                    MediaType.ANIME -> "%.2f".format(minutesWatched.minutesToDays())
                    MediaType.MANGA -> volumesRead.toString()
                },
                statType = OverviewStatType.TIME
            ),
            ShortOverviewStat(
                count = when(mediaType) {
                    MediaType.ANIME -> {
                        if(plannedStatus?.minutesWatched != null) {
                            "%.2f".format(plannedStatus.minutesWatched.minutesToDays())
                        } else {
                            0f.toString()
                        }
                    }
                    MediaType.MANGA -> {
                        (plannedStatus?.chaptersRead ?: 0f).toString()
                    }
                },
                statType = OverviewStatType.PLANNED
            ),
            ShortOverviewStat(
                count = meanScore.toString(),
                statType = OverviewStatType.MEAN_SCORE
            ),
            ShortOverviewStat(
                count = standardDeviation.toString(),
                statType = OverviewStatType.STANDARD_DEVIATION
            )
        )
    }

    fun ALUserListStats.toScoreStatsTitles(): List<Stat<Int>> {
        return scores?.mapNotNull { score ->
            Stat<Int>(
                type = score?.score ?: 0,
                value = score?.count?.toFloat() ?: 0f
            )
        }?.sortedBy { it.type } ?: emptyList()
    }

    fun ALUserListStats.toScoreStatsTime(mediaType: MediaType): List<Stat<Int>> {
        return scores?.mapNotNull { score ->
            Stat<Int>(
                type = score?.score ?: 0,
                value = when(mediaType) {
                    MediaType.ANIME -> score?.minutesWatched?.div(60)
                    MediaType.MANGA -> score?.chaptersRead
                }?.toFloat() ?: 0f
            )
        }?.sortedBy { it.type } ?: emptyList()
    }

    fun ALUserListStats.toStatusesStats(): List<Stat<UserRateStatus>> {
        return statuses?.mapNotNull { status ->
            Stat<UserRateStatus>(
                type = status?.status?.toDomain() ?: UserRateStatus.UNKNOWN,
                value = status?.count?.toFloat() ?: 0f
            )
        }?.sortedBy { UserRateStatus.entries.indexOf(it.type) } ?: emptyList()
    }

    fun ALUserListStats.toLengthStatsTitles(): List<Stat<String>> {
        return lengths?.mapNotNull { length ->
            Stat<String>(
                type = length?.length ?: "",
                value = length?.count?.toFloat() ?: 0f
            )
        }?.sortedBy { length ->
            length.type.trimEnd('+').split("-").first().toIntOrNull() ?: 0
        } ?: emptyList()
    }

    fun ALUserListStats.toLengthStatsTime(mediaType: MediaType): List<Stat<String>> {
        return lengths?.mapNotNull { length ->
            Stat<String>(
                type = length?.length ?: "",
                value = when(mediaType) {
                    MediaType.ANIME -> length?.minutesWatched?.div(60)
                    MediaType.MANGA -> length?.chaptersRead
                }?.toFloat() ?: 0f
            )
        }?.sortedBy { length ->
            length.type.trimEnd('+').split("-").first().toIntOrNull() ?: 0
        } ?: emptyList()
    }

    fun ALUserListStats.toLengthStatsScore(): List<Stat<String>> {
        return lengths?.mapNotNull { length ->
            Stat<String>(
                type = length?.length ?: "",
                value = length?.meanScore?.toFloat() ?: 0f
            )
        }?.sortedBy { length ->
            length.type.trimEnd('+').split("-").first().toIntOrNull() ?: 0
        } ?: emptyList()
    }

    fun ALUserListStats.toFormatStats(): List<Stat<MediaFormat>> {
        return formats?.mapNotNull { format ->
            Stat<MediaFormat>(
                type = format?.format?.toDomain() ?: MediaFormat.UNKNOWN,
                value = format?.count?.toFloat() ?: 0f
            )
        }?.sortedBy { format ->
            format.type.ordinal
        } ?: emptyList()
    }

    fun ALUserListStats.toCountryStats(): List<Stat<CountryOfOrigin>> {
        return countries?.mapNotNull { country ->
            Stat<CountryOfOrigin>(
                type = country?.country?.toCountryOfOrigin() ?: CountryOfOrigin.JAPAN,
                value = country?.count?.toFloat() ?: 0f
            )
        }?.sortedBy { format ->
            format.type.ordinal
        } ?: emptyList()
    }

    fun ALUserListStats.toReleaseYearStatsCount(): List<Stat<Int>> {
        return releaseYears?.mapNotNull { releaseYear ->
            Stat<Int>(
                type = releaseYear?.releaseYear ?: 0,
                value = releaseYear?.count?.toFloat() ?: 0f
            )
        }?.sortedBy { stat ->
            stat.type
        } ?: emptyList()
    }

    fun ALUserListStats.toReleaseYearStatsTime(mediaType: MediaType): List<Stat<Int>> {
        return releaseYears?.mapNotNull { releaseYear ->
            Stat<Int>(
                type = releaseYear?.releaseYear ?: 0,
                value = when(mediaType) {
                    MediaType.ANIME -> releaseYear?.minutesWatched?.div(60)
                    MediaType.MANGA -> releaseYear?.chaptersRead
                }?.toFloat() ?: 0f
            )
        }?.sortedBy { stat ->
            stat.type
        } ?: emptyList()
    }

    fun ALUserListStats.toReleaseYearStatsScore(): List<Stat<Int>> {
        return releaseYears?.mapNotNull { releaseYear ->
            Stat<Int>(
                type = releaseYear?.releaseYear ?: 0,
                value = releaseYear?.meanScore?.toFloat() ?: 0f
            )
        }?.sortedBy { stat ->
            stat.type
        } ?: emptyList()
    }

    fun ALUserListStats.toStartYearStatsCount(): List<Stat<Int>> {
        return startYears?.mapNotNull { startYear ->
            Stat<Int>(
                type = startYear?.startYear ?: 0,
                value = startYear?.count?.toFloat() ?: 0f
            )
        }?.sortedBy { stat ->
            stat.type
        } ?: emptyList()
    }

    fun ALUserListStats.toStartYearStatsTime(mediaType: MediaType): List<Stat<Int>> {
        return startYears?.mapNotNull { startYear ->
            Stat<Int>(
                type = startYear?.startYear ?: 0,
                value = when(mediaType) {
                    MediaType.ANIME -> startYear?.minutesWatched?.div(60)
                    MediaType.MANGA -> startYear?.chaptersRead
                }?.toFloat() ?: 0f
            )
        }?.sortedBy { stat ->
            stat.type
        } ?: emptyList()
    }

    fun ALUserListStats.toStartYearStatsScore(): List<Stat<Int>> {
        return startYears?.mapNotNull { startYear ->
            Stat<Int>(
                type = startYear?.startYear ?: 0,
                value = startYear?.meanScore?.toFloat() ?: 0f
            )
        }?.sortedBy { stat ->
            stat.type
        } ?: emptyList()
    }

    fun ALUserGenres.toGenreStats(): List<TypeStat> {
        return genres?.mapNotNull { genreStat ->
            TypeStat(
                type = genreStat?.genre ?: "",
                count = genreStat?.count ?: 0,
                meanScore = genreStat?.meanScore?.toFloat() ?: 0f,
                timeWatched = genreStat?.minutesWatched?.minutesToDays() ?: 0f,
                chaptersRead = genreStat?.chaptersRead ?: 0
            )
        } ?: emptyList()
    }

    fun ALUserTags.toTagsStats(): List<TypeStat> {
        return tags?.mapNotNull { tagStat ->
            TypeStat(
                type = tagStat?.tag?.name ?: "",
                count = tagStat?.count ?: 0,
                meanScore = tagStat?.meanScore?.toFloat() ?: 0f,
                timeWatched = tagStat?.minutesWatched?.minutesToDays() ?: 0f,
                chaptersRead = tagStat?.chaptersRead ?: 0
            )
        } ?: emptyList()
    }

    fun ALUserStaff.toStaffStats(): List<StaffStat> {
        return staff?.mapNotNull { staffStat ->
            staffStat?.staff?.aLStaffShort?.let { staffShort ->
                StaffStat(
                    staffShort = staffShort.toDomain(),
                    count = staffStat.count,
                    meanScore = staffStat.meanScore.toFloat(),
                    timeWatched = staffStat.minutesWatched.minutesToDays(),
                    chaptersRead = staffStat.chaptersRead
                )
            }
        } ?: emptyList()
    }

    fun ALUserVoiceActors.toStaffStats(): List<StaffStat> {
        return voiceActors?.mapNotNull { vaStat ->
            vaStat?.voiceActor?.aLStaffShort?.let { staffShort ->
                StaffStat(
                    staffShort = staffShort.toDomain(),
                    count = vaStat.count,
                    meanScore = vaStat.meanScore.toFloat(),
                    timeWatched = vaStat.minutesWatched.minutesToDays(),
                    chaptersRead = vaStat.chaptersRead
                )
            }
        } ?: emptyList()
    }

    fun ALUserStudios.toStudiosStats(): List<StudioStat> {
        return studios?.mapNotNull { studioStat ->
            studioStat?.studio?.aLStudioShort?.let { studio ->
                StudioStat(
                    studioShort = studio.toDomain(),
                    count = studioStat.count,
                    meanScore = studioStat.meanScore.toFloat(),
                    timeWatched = studioStat.minutesWatched.minutesToDays(),
                    chaptersRead = 0
                )
            }
        } ?: emptyList()
    }
}