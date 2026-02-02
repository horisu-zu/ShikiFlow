package com.example.shikiflow.domain.model.thread

import com.example.shikiflow.domain.model.common.SortDirection

data class ThreadParams(
    val mediaId: Int? = null,
    val sort: ThreadSort = ThreadSort(ThreadSortType.ID, SortDirection.DESCENDING)
)
