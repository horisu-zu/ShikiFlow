package com.example.shikiflow.domain.model.user.stats

interface CombinedStat {
    val count: Int
    val meanScore: Float
    val timeWatched: Float
    val chaptersRead: Int
}