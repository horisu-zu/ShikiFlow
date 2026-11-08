package com.example.shikiflow.domain.model.comment

import com.example.shikiflow.domain.model.user.activity.ActivityType
import com.example.shikiflow.domain.model.user.UserShort as DomainUser

data class EntityData(
    val id: String,
    val type: EntityType
)

sealed interface EntityType {
    data object Character : EntityType
    data object Staff : EntityType
    data object Anime : EntityType
    data object Manga : EntityType
    data object Ranobe : EntityType
    data object Thread : EntityType
    data object CommentReply : EntityType
    data object Review : EntityType
    data class User(val user: DomainUser) : EntityType
    data class Activity(val type: ActivityType) : EntityType
}