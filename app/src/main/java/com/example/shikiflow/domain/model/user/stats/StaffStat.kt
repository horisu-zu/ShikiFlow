package com.example.shikiflow.domain.model.user.stats

import com.example.shikiflow.domain.model.media_details.MediaPersonShort

data class StaffStat(
    val staffShort: MediaPersonShort,
    override val count: Int,
    override val meanScore: Float,
    override val timeWatched: Float,
    override val chaptersRead: Int
): CombinedStat
