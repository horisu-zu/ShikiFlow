package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.anilist.AnilistStaffDataSource
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.staff.StaffShort

class MediaStaffPagingSource(
    private val mediaId: Int,
    private val sort: Sort<StaffType>,
    private val staffDataSource: AnilistStaffDataSource
): PagingSource<Int, StaffShort>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StaffShort> {
        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        val result = staffDataSource.paginatedMediaStaff(
            mediaId = mediaId,
            sort = sort,
            page = currentPage,
            limit = pageSize
        )

        return result.fold(
            onSuccess = { response ->
                val prevKey =  if(currentPage > 1) currentPage - 1 else null
                val nextKey = if(response.size < pageSize) null else currentPage + 1

                val groupedData = response
                    .groupBy { it.id }
                    .mapValues { (_, staffRoles) ->
                        val staffData = staffRoles.first()

                        StaffShort(
                            id = staffData.id,
                            fullName = staffData.fullName,
                            imageUrl = staffData.imageUrl,
                            roles = staffRoles.flatMap { it.roles },
                        )
                    }.values
                    .toList()

                LoadResult.Page(
                    data = groupedData,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            },
            onFailure = { e ->
                LoadResult.Error(e)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, StaffShort>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}