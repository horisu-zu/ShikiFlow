package com.example.shikiflow.domain.model.common

import kotlin.collections.emptyList

data class PaginatedList<T> (
    val hasNextPage: Boolean,
    val entries: List<T>
) {
    companion object {
        fun <T> emptyPaginatedList(): PaginatedList<T> = PaginatedList(
            hasNextPage = false,
            entries = emptyList()
        )
    }
}
