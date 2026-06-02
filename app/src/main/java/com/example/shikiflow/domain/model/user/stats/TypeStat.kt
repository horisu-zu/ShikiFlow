package com.example.shikiflow.domain.model.user.stats

data class TypeStat<T>(
    val type: T,
    override val count: Int,
    override val meanScore: Float,
    override val timeWatched: Float,
    override val chaptersRead: Int
): CombinedStat
