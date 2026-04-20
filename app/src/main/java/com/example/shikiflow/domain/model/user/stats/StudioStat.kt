package com.example.shikiflow.domain.model.user.stats

import com.example.shikiflow.domain.model.media_details.StudioShort

data class StudioStat(
    val studioShort: StudioShort,
    override val count: Int,
    override val meanScore: Float,
    override val timeWatched: Float,
    override val chaptersRead: Int
): CombinedStat
