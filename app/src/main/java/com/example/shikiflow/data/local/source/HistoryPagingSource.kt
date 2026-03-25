package com.example.shikiflow.data.local.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.domain.model.user.UserActivity
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HistoryPagingSource @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userId: Int
): PagingSource<Int, UserActivity>() {

    override fun getRefreshKey(state: PagingState<Int, UserActivity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserActivity> {
        return  try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            val response = userDataSource.getPaginatedHistory(
                userId = userId,
                page = page,
                limit = pageSize
            )

            val prevKey =  if(page > 1) page - 1 else null
            val nextKey = if (response.size < pageSize) {
                null
            } else page + 1

            val responseSize = response.size
            Log.d("HistoryPagingSource", "Received $responseSize items for page $page, requested ${params.loadSize}")

            LoadResult.Page(
                data = response,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}