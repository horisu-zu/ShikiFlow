package com.example.shikiflow.presentation.viewmodel.comment.threads

import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.ThreadType

data class ThreadParams(
    val mediaId: Int? = null,
    val sort: Sort<ThreadType> = Sort(ThreadType.CREATED_AT, SortDirection.DESCENDING)
)