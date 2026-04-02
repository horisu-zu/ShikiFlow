package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState

class GenericPagingSource<T : Any>(
    private val method: suspend (page: Int, limit: Int) -> Result<List<T>>
): PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        return try {
            val response = method(page, pageSize)

            response.fold(
                onSuccess = { data ->
                    val prevKey = if (page > 1) page - 1 else null
                    val nextKey = if (data.size >= pageSize) page + 1 else null

                    LoadResult.Page(
                        data = data,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                },
                onFailure = { throwable ->
                    LoadResult.Error(throwable)
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}