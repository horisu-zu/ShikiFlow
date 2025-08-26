package com.example.shikiflow.domain.model.anime

import com.example.graphql.type.UserRateStatusEnum

enum class MyListString {
    PLANNED,
    WATCHING,
    REWATCHING,
    COMPLETED,
    ON_HOLD,
    DROPPED
}

fun MyListString.toReadableString(): String = when(this) {
    MyListString.PLANNED -> "Planned"
    MyListString.WATCHING -> "Watching"
    MyListString.REWATCHING -> "Rewatching"
    MyListString.COMPLETED -> "Completed"
    MyListString.ON_HOLD -> "On Hold"
    MyListString.DROPPED -> "Dropped"
}

fun MyListString.toUserRateStatusEnum(): UserRateStatusEnum = when(this) {
    MyListString.PLANNED -> UserRateStatusEnum.planned
    MyListString.WATCHING -> UserRateStatusEnum.watching
    MyListString.REWATCHING -> UserRateStatusEnum.rewatching
    MyListString.COMPLETED -> UserRateStatusEnum.completed
    MyListString.ON_HOLD -> UserRateStatusEnum.on_hold
    MyListString.DROPPED -> UserRateStatusEnum.dropped
}

fun MyListString.toGraphQLValue(): String = name.lowercase()