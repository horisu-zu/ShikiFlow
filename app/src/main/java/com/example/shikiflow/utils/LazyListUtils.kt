package com.example.shikiflow.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow

@SuppressLint("ComposableNaming")
object LazyListUtils {

    @Composable
    fun LazyListState.onBottomReached(
        buffer: Int,
        onLoadMore: suspend () -> Unit
    ) {
        require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

        val shouldLoadMore = remember {
            derivedStateOf {
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                    ?: return@derivedStateOf true

                lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
            }
        }

        LaunchedEffect(shouldLoadMore) {
            snapshotFlow { shouldLoadMore.value }
                .collect { if (it) onLoadMore() }
        }
    }

    @Composable
    fun LazyGridState.onBottomReached(
        buffer: Int,
        onLoadMore: suspend () -> Unit
    ) {
        require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

        val shouldLoadMore = remember {
            derivedStateOf {
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                    ?: return@derivedStateOf true

                lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
            }
        }

        LaunchedEffect(shouldLoadMore) {
            snapshotFlow { shouldLoadMore.value }
                .collect { if (it) onLoadMore() }
        }
    }
}