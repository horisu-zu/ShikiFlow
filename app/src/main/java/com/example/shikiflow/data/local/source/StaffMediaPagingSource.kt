package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.anilist.AnilistStaffDataSource
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.tracks.MediaType
import javax.inject.Inject

class StaffMediaPagingSource @Inject constructor(
    private val staffId: Int,
    private val mediaType: MediaType,
    private val staffDataSource: AnilistStaffDataSource
): PagingSource<Int, MediaRole>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaRole> {
        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        val result = staffDataSource.paginatedStaffMediaRoles(
            page = currentPage,
            limit = pageSize,
            staffId = staffId,
            mediaType = mediaType
        )

        return result.fold(
            onSuccess = { response ->
                val prevKey =  if(currentPage > 1) currentPage - 1 else null
                val nextKey = if(response.isNotEmpty()) currentPage + 1 else null

                LoadResult.Page(
                    data = response,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            },
            onFailure = { e ->
                LoadResult.Error(e)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, MediaRole>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
