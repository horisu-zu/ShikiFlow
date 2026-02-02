package com.example.shikiflow.domain.model.thread

import com.example.shikiflow.domain.model.common.SortDirection

data class ThreadSort(
    val threadSortType: ThreadSortType,
    val sortDirection: SortDirection
)

enum class ThreadSortType {
    ID,
    TITLE,
    CREATED_AT,
    REPLIED_AT,
    REPLY_COUNT,
    VIEW_COUNT
}
