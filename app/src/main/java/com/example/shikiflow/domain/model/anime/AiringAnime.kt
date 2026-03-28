package com.example.shikiflow.domain.model.anime

import com.example.shikiflow.domain.model.common.ShortMedia
import kotlin.time.Duration
import kotlin.time.Instant

data class AiringAnime(
    val data: ShortMedia,
    val episode: Int,
    val timeUntilAiring: Duration?,
    val airingAt: Instant?,
    val releasedOn: Instant? = null
)
