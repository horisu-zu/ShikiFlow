package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.MediaDetailsDataSource
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.track.OrderOption

class StudioMediaPagingSource(
    private val mediaDetailsDataSource: MediaDetailsDataSource,
    private val studioId: Int,
    private val search: String?,
    private val order: OrderOption,
    private val onList: Boolean?
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

        val response = mediaDetailsDataSource.loadStudioMedia(
            studioId,
            page = currentPage,
            limit = pageSize,
            search = search,
            order = order,
            onList = onList
        )

        return response.fold(
            onSuccess = { result ->
                LoadResult.Page(
                    data = result,
                    prevKey = if (currentPage > 1) currentPage - 1 else null,
                    nextKey = if (result.size < pageSize) null else currentPage + 1
                )
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            }
        )
    }
}