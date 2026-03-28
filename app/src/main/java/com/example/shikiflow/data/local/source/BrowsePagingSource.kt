package com.example.shikiflow.data.local.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.search.BrowseOptions
import javax.inject.Inject

class BrowsePagingSource @Inject constructor(
    private val mediaDataSource: MediaDataSource,
    private val options: BrowseOptions
): PagingSource<Int, Browse>() {

    override fun getRefreshKey(state: PagingState<Int, Browse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Browse> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        val response = mediaDataSource.browseMedia(
            page = page,
            limit = pageSize,
            browseOptions = options
        )

        return response.fold(
            onSuccess = { result ->
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (result.size == pageSize) page + 1 else null

                LoadResult.Page(
                    data = result,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            },
            onFailure = { error ->
                Log.d("BrowsePagingSource", "Error: ${error.message}")
                LoadResult.Error(error)
            }
        )
    }
}