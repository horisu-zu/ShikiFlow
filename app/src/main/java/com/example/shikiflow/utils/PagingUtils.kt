package com.example.shikiflow.utils

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

object PagingUtils {
    fun <T: Any> LazyPagingItems<T>.isInitialLoad(): Boolean {
        return loadState.refresh is LoadState.Loading ||
            (
                loadState.refresh is LoadState.NotLoading &&
                !loadState.append.endOfPaginationReached &&
                this.itemCount == 0
            )
    }

    fun <T: Any> LazyPagingItems<T>.fetched(): Boolean {
        return loadState.append.endOfPaginationReached || this.itemCount > 0
    }
}