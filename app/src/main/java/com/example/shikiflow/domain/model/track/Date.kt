package com.example.shikiflow.domain.model.track

import kotlin.time.Instant

data class Date(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
    val date: Instant? = null
)
