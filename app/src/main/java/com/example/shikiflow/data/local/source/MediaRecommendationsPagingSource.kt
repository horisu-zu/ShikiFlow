package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.tracks.MediaType

class MediaRecommendationsPagingSource(
    private val mediaDataSource: MediaDataSource,
    private val mediaType: MediaType,
    private val mediaId: Int
): PagingSource<Int, Browse>() {
    override fun getRefreshKey(state: PagingState<Int, Browse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Browse> {
        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        val result = mediaDataSource.loadMediaRecommendations(
            mediaType,
            mediaId,
            page = currentPage,
            limit = pageSize
        )

        return result.fold(
            onSuccess = { responseData ->
                LoadResult.Page(
                    data = responseData,
                    prevKey = if (currentPage > 1) currentPage - 1 else null,
                    nextKey = if (responseData.size < pageSize) null else currentPage + 1
                )
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            }
        )
    }
}