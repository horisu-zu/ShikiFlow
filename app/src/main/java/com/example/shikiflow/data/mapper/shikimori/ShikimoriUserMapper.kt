package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.fragment.UserShort
import com.example.graphql.shikimori.type.AnimeKindEnum
import com.example.graphql.shikimori.type.MangaKindEnum
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
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.ListActivity
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.fleeksoft.ksoup.Ksoup
import kotlin.time.Instant

object ShikimoriUserMapper {
    fun UserShort.toDomain(): User {
        return User(
            id = this.id.toInt(),
            nickname = this.nickname,
            avatarUrl = this.avatarUrl,
            lastOnlineAt = Instant.parse(this.lastOnlineAt.toString())
        )
    }

    fun ShikiUser.toDomain(): User {
        return User(
            id = this.id,
            nickname = this.nickname,
            avatarUrl = this.avatar.replace("/x48/", "/x160/"),
            lastOnlineAt = Instant.parse(this.lastOnlineAt.toString())
        )
    }

    fun ShikiHistoryResponse.toDomain(): ListActivity {
        val mediaType = when {
            AnimeKindEnum.entries.any { it.name == target?.kind } -> MediaType.ANIME
            MangaKindEnum.entries.any { it.name == target?.kind } -> MediaType.MANGA
            else -> null
        }

        return ListActivity(
            id = id.toInt(),
            mediaId = target?.id?.toInt() ?: 0,
            mediaType = mediaType,
            title = target?.name ?: "",
            coverImage = "${BuildConfig.SHIKI_BASE_URL}${target?.image?.original}",
            description = Ksoup.parse(description).text(),
            createdAt = Instant.parse(createdAt)
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
        userFavorites: List<UserFavorite>,
        friends: List<User>
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

        val friends = if(friends.isNotEmpty()) SocialCategory.FOLLOWINGS else null

        return UserStatsCategories(
            scoreMediaTypes = mediaTypes,
            favoriteCategories = categories,
            socialCategories = listOfNotNull(friends)
        )
    }
}