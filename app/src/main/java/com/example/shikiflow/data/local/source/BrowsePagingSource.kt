package com.example.shikiflow.data.local.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import javax.inject.Inject

class BrowsePagingSource @Inject constructor(
    private val mediaDataSource: MediaDataSource,
    private val options: MediaBrowseOptions
): PagingSource<Int, BrowseMedia>() {

    override fun getRefreshKey(state: PagingState<Int, BrowseMedia>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BrowseMedia> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        if(options.name?.isBlank() == true) return LoadResult.Page(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )

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