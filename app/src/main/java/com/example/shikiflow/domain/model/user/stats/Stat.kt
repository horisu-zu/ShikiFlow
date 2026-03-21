package com.example.shikiflow.domain.model.user.stats

data class Stat<T>(
    val type: T,
    val value: Float
)
