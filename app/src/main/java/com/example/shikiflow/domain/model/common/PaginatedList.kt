package com.example.shikiflow.domain.model.common

data class PaginatedList<T> (
    val hasNextPage: Boolean,
    val entries: List<T>
)
