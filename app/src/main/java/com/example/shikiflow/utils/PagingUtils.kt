package com.example.shikiflow.utils

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

object PagingUtils {
    fun <T: Any> LazyPagingItems<T>.isLoading(): Boolean {
        return loadState.refresh is LoadState.Loading ||
            (
                loadState.refresh is LoadState.NotLoading &&
                !loadState.append.endOfPaginationReached &&
                itemCount == 0
            )
    }

    fun <T: Any> LazyPagingItems<T>.fetched(): Boolean {
        return loadState.append.endOfPaginationReached || itemCount > 0
    }
}