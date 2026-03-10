package com.example.shikiflow.domain.model.track

import kotlin.time.Instant

data class Date(
    val year: Int,
    val month: Int,
    val day: Int,
    val date: Instant? = null
)
