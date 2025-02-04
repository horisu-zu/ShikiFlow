package com.example.shikiflow.data.mapper

object UserRateStatusConstants {
    val chips = listOf("Watching", "Planned", "Completed", "Rewatching", "On Hold", "Dropped")

    fun convertToApiStatus(index: Int): String = when(index) {
        0 -> "watching"
        1 -> "planned"
        2 -> "completed"
        3 -> "rewatching"
        4 -> "on_hold"
        5 -> "dropped"
        else -> "planned"
    }

    fun convertFromApiStatus(status: String): Int = when(status.lowercase()) {
        "watching" -> 0
        "planned" -> 1
        "completed" -> 2
        "rewatching" -> 3
        "on_hold" -> 4
        "dropped" -> 5
        else -> 1
    }
}