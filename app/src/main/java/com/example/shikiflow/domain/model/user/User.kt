package com.example.shikiflow.domain.model.user

import com.example.graphql.CurrentUserQuery
import com.example.graphql.UsersQuery
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class User(
    val id: String,
    val avatarUrl: String,
    val nickname: String,
    @Contextual val lastOnlineAt: Instant? = null
) {
    companion object {
        fun CurrentUserQuery.CurrentUser.toDomain(): User {
            return User(
                id = this.id,
                nickname = this.nickname,
                avatarUrl = this.avatarUrl,
                lastOnlineAt = Instant.parse(this.lastOnlineAt.toString())
            )
        }

        fun UsersQuery.User.toDomain(): User {
            return User(
                id = this.id,
                nickname = this.nickname,
                avatarUrl = this.avatarUrl,
                lastOnlineAt = Instant.parse(this.lastOnlineAt.toString())
            )
        }
    }
}