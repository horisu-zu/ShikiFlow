package com.example.shikiflow.domain.model.sort

data class Sort<out T : SortType>(
    val type: T,
    val direction: SortDirection
)