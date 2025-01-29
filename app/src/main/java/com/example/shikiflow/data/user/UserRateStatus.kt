package com.example.shikiflow.data.user

import android.util.Log

enum class UserRateContentType {
    ANIME,
    MANGA
}

val animeStatusOrder = listOf(
    "planned",
    "watching",
    "completed",
    "rewatching",
    "on_hold",
    "dropped"
)

val animeToMangaStatusMap = mapOf(
    "watching" to "reading",
    "rewatching" to "rereading"
)

val mangaStatusOrder = listOf(
    "planned",
    "reading",
    "completed",
    "rereading",
    "on_hold",
    "dropped"
)
