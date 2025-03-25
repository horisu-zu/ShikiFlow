package com.example.shikiflow.data.mapper

import com.example.shikiflow.data.tracks.MediaType

object UserRateStatusConstants {
    private val statusOrderMap = mapOf(
        MediaType.ANIME to listOf(
            "planned", "watching", "completed",
            "rewatching", "on_hold", "dropped"
        ),
        MediaType.MANGA to listOf(
            "planned", "reading", "completed",
            "rereading", "on_hold", "dropped"
        )
    )

    private val statusConvertMap = mapOf(
        "watching" to "reading",
        "rewatching" to "rereading"
    )

    private val statusChipsMap = mapOf(
        MediaType.ANIME to listOf(
            "Watching", "Planned", "Completed",
            "Rewatching", "On Hold", "Dropped"
        ),
        MediaType.MANGA to listOf(
            "Reading", "Planned", "Completed",
            "Rereading", "On Hold", "Dropped"
        )
    )

    fun convertStatus(status: String): String =
        statusConvertMap[status] ?: status

    fun getStatusOrder(contentType: MediaType): List<String> =
        statusOrderMap[contentType] ?: emptyList()

    fun getStatusChips(contentType: MediaType): List<String> =
        statusChipsMap[contentType] ?: emptyList()

    fun convertToApiStatus(index: Int): String = when(index) {
        0 -> "watching"
        1 -> "planned"
        2 -> "completed"
        3 -> "rewatching"
        4 -> "on_hold"
        5 -> "dropped"
        else -> "planned"
    }
}