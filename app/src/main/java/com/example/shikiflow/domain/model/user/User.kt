package com.example.shikiflow.domain.model.user

import com.example.graphql.CurrentUserQuery

data class User(
    val id: String,
    val avatarUrl: String,
    val nickname: String
) {
    companion object {
        fun CurrentUserQuery.CurrentUser.toDomain(): User {
            return User(
                id = this.id,
                nickname = this.nickname,
                avatarUrl = this.avatarUrl
            )
        }
    }
}