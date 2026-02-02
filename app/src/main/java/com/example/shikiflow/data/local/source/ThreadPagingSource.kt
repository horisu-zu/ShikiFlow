package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadSort
import javax.inject.Inject

class ThreadPagingSource @Inject constructor(
    private val commentsDataSource: CommentsDataSource,
    private val mediaId: Int,
    private val threadSort: ThreadSort
): PagingSource<Int, Thread>() {
    override fun getRefreshKey(state: PagingState<Int, Thread>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Thread> {
        val page = params.key ?: 1

        val response = commentsDataSource.getMediaThreads(
            mediaId = mediaId,
            page = page,
            limit = params.loadSize,
            threadSort = threadSort
        )

        return response.fold(
            onSuccess = { result ->
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (result.size < params.loadSize) null else page + 1

                LoadResult.Page(
                    data = result,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            }
        )
    }
}