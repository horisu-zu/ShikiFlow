package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.fragment.UserShort
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.ShikiHistoryResponse
import com.example.shikiflow.data.datasource.dto.ShikiUserFavoritesResponse
import com.example.shikiflow.data.datasource.dto.ShikiAnime
import com.example.shikiflow.data.datasource.dto.ShikiManga
import com.example.shikiflow.data.datasource.dto.ShikiCharacter
import com.example.shikiflow.data.datasource.dto.comment.ShikiUser
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.data.datasource.dto.person.ShikiPerson
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.MediaTypeStats
import com.example.shikiflow.domain.model.user.OverviewStats
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.UserHistory
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.fleeksoft.ksoup.Ksoup
import kotlin.time.Instant

object ShikimoriUserMapper {
    fun UserShort.toDomain(): User {
        return User(
            id = this.id,
            nickname = this.nickname,
            avatarUrl = this.avatarUrl,
            lastOnlineAt = Instant.parse(this.lastOnlineAt.toString())
        )
    }

    fun ShikiUser.toDomain(): User {
        return User(
            id = this.id.toString(),
            nickname = this.nickname,
            avatarUrl = this.avatar,
            lastOnlineAt = Instant.parse(this.lastOnlineAt.toString())
        )
    }

    fun ShikiHistoryResponse.toDomain(): UserHistory {
        return UserHistory(
            id = this.id.toInt(),
            mediaId = this.target?.id?.toInt() ?: 0,
            title = this.target?.name ?: "",
            coverImage = "${BuildConfig.SHIKI_BASE_URL}${this.target?.image?.original}",
            description = Ksoup.parse(this.description).text(),
            createdAt = Instant.parse(this.createdAt)
        )
    }

    fun ShikiUserFavoritesResponse.toDomain(): List<UserFavorite> {
        return buildList {
            animeList?.let { addAll(it.map { anime -> anime.toUserFavorite() }) }
            mangaList?.let { addAll(it.map { manga -> manga.toUserFavorite() }) }
            ranobeList?.let { addAll(it.map { ranobe -> ranobe.toUserFavorite() }) }
            characters?.let { addAll(it.map { character -> character.toUserFavorite() }) }
            mangakas?.let { addAll(it.map { mangaka -> mangaka.toUserFavorite(FavoriteCategory.MANGAKA) }) }
            seyuList?.let { addAll(it.map { seyu -> seyu.toUserFavorite(FavoriteCategory.SEYU) }) }
            producers?.let { addAll(it.map { producer -> producer.toUserFavorite(FavoriteCategory.PRODUCER) }) }
            people?.let { addAll(it.map { person -> person.toUserFavorite(FavoriteCategory.OTHER_PERSON) }) }
        }
    }

    fun ShikiAnime.toUserFavorite() = UserFavorite(
        id = id ?: 0,
        name = name ?: "",
        imageUrl = BuildConfig.SHIKI_BASE_URL + image?.original?.replace("/x64/", "/original/"),
        favoriteCategory = FavoriteCategory.ANIME
    )

    fun ShikiManga.toUserFavorite() = UserFavorite(
        id = id ?: 0,
        name = name ?: "",
        imageUrl = BuildConfig.SHIKI_BASE_URL + image?.original?.replace("/x64/", "/original/"),
        favoriteCategory = FavoriteCategory.MANGA
    )

    fun ShikiCharacter.toUserFavorite() = UserFavorite(
        id = id,
        name = name,
        imageUrl = BuildConfig.SHIKI_BASE_URL + image.original?.replace("/x64/", "/original/"),
        favoriteCategory = FavoriteCategory.CHARACTER
    )

    fun ShikiPerson.toUserFavorite(favoriteCategory: FavoriteCategory) = UserFavorite(
        id = id,
        name = name,
        imageUrl = BuildConfig.SHIKI_BASE_URL + image.original?.replace("/x64/", "/original/"),
        favoriteCategory = favoriteCategory
    )

    fun mapUserStats(
        mediaTypeStats: MediaTypeStats<OverviewStats>,
        userFavorites: List<UserFavorite>
    ): UserStatsCategories {
        val mediaTypes = buildList {
            mediaTypeStats.animeStats?.scoreStatsTitles?.let { animeScores ->
                if(animeScores.isNotEmpty()) {
                    add(MediaType.ANIME)
                }
            }
            mediaTypeStats.mangaStats?.scoreStatsTitles?.let { animeScores ->
                if(animeScores.isNotEmpty()) {
                    add(MediaType.MANGA)
                }
            }
        }

        val categories = userFavorites.map { it.favoriteCategory }.distinct()

        return UserStatsCategories(
            scoreMediaTypes = mediaTypes,
            favoriteCategories = categories
        )
    }
}