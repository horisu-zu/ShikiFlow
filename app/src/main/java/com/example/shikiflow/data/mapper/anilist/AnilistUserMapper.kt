package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.UserStatsCategoriesQuery
import com.example.graphql.anilist.fragment.ALFavoriteCharacterShort
import com.example.graphql.anilist.fragment.ALFavoriteMediaShort
import com.example.graphql.anilist.fragment.ALFavoriteStaffShort
import com.example.graphql.anilist.fragment.ALFavoriteStudioShort
import com.example.graphql.anilist.fragment.ALUserActivity
import com.example.graphql.anilist.fragment.ALUserListStats
import com.example.graphql.anilist.fragment.ALUserShort
import com.example.graphql.anilist.type.MediaListStatus
import com.example.shikiflow.data.mapper.common.DateMapper.minutesToDays
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.OverviewStatType
import com.example.shikiflow.domain.model.user.OverviewStats
import com.example.shikiflow.domain.model.user.ShortOverviewStat
import com.example.shikiflow.domain.model.user.Stat
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.UserHistory
import com.example.shikiflow.domain.model.user.UserStatsCategories
import kotlin.time.Instant

object AnilistUserMapper {
    fun ALUserShort.toDomain(): User {
        return User(
            id = this.id.toString(),
            nickname = this.name,
            avatarUrl = this.avatar?.large ?: "",
            profileBannerUrl = this.bannerImage
        )
    }

    fun ALUserActivity.toDomain(): UserHistory {
        return UserHistory(
            id = this.id,
            mediaId = this.id,
            title = this.media?.title?.romaji ?: "",
            coverImage = this.media?.coverImage?.extraLarge ?: "",
            description = buildString {
                this@toDomain.status?.let { status ->
                    append(status.replaceFirstChar { it.uppercase() })
                }
                this@toDomain.progress?.let { progress ->
                    append(" $progress")
                }
            },
            createdAt = Instant.fromEpochSeconds(this.createdAt.toLong())
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

    fun UserStatsCategoriesQuery.User.toDomain(): UserStatsCategories {
        val mediaTypes = buildList {
            statistics?.anime?.count?.let { userCount ->
                if(userCount > 0) {
                    add(MediaType.ANIME)
                }
            }
            statistics?.manga?.count?.let { userCount ->
                if(userCount > 0) {
                    add(MediaType.MANGA)
                }
            }
        }

        val categories = mapOf(
            FavoriteCategory.ANIME to (favourites?.anime?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.MANGA to (favourites?.manga?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.CHARACTER to (favourites?.characters?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.STAFF to (favourites?.staff?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.STUDIO to (favourites?.studios?.pageInfo?.aLPageInfoShort?.total ?: 0)
        )
            .filter { it.value > 0 }
            .keys
            .toList()

        return UserStatsCategories(
            scoreMediaTypes = mediaTypes,
            favoriteCategories = categories
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
            lengthStatsScore = toLengthStatsScore()
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
                    MediaType.ANIME -> "%.2f".format(plannedStatus?.minutesWatched?.minutesToDays())
                    MediaType.MANGA -> plannedStatus?.chaptersRead.toString()
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
                    MediaType.ANIME -> score?.minutesWatched?.div(60)?.toFloat() ?: 0f
                    MediaType.MANGA -> score?.chaptersRead?.toFloat() ?: 0f
                }
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
                    MediaType.ANIME -> length?.minutesWatched?.div(60)?.toFloat() ?: 0f
                    MediaType.MANGA -> length?.chaptersRead?.toFloat() ?: 0f
                }
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
}