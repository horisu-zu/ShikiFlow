package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.fragment.ALFavoriteCharacterShort
import com.example.graphql.anilist.fragment.ALFavoriteMediaShort
import com.example.graphql.anilist.fragment.ALFavoriteStaffShort
import com.example.graphql.anilist.fragment.ALFavoriteStudioShort
import com.example.graphql.anilist.fragment.ALUserActivity
import com.example.graphql.anilist.fragment.ALUserShort
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.UserHistory
import kotlin.time.Instant

object AnilistUserMapper {
    fun ALUserShort.toDomain(): User {
        return User(
            id = this.id.toString(),
            nickname = this.name,
            avatarUrl = this.avatar?.large ?: ""
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
}