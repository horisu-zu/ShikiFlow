package com.example.shikiflow.domain.model.track

enum class UserRateStatus {
    WATCHING,
    PLANNED,
    COMPLETED,
    REWATCHING,
    PAUSED,
    DROPPED,
    UNKNOWN;

    companion object {
        fun isWatched(status: UserRateStatus): Boolean {
            return setOf(
                WATCHING,
                DROPPED,
                PAUSED
            ).contains(status)
        }
    }
}