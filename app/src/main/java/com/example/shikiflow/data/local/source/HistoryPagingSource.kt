package com.example.shikiflow.data.local.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.domain.model.user.UserHistoryResponse
import com.example.shikiflow.domain.repository.UserRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HistoryPagingSource @Inject constructor(
    private val userRepository: UserRepository,
    private val userId: Long
): PagingSource<Int, UserHistoryResponse>() {
    override fun getRefreshKey(state: PagingState<Int, UserHistoryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserHistoryResponse> {
        return  try {
            val page = params.key ?: 0

            val response = userRepository.getUserHistory(
                userId = userId,
                page = page,
                limit = params.loadSize
            )

            val prevKey =  if(page > 0) page - 1 else null
            val nextKey = if (response.isSuccess && response.getOrNull()?.isNotEmpty() == true) {
                page + 1
            } else null

            val responseSize = response.getOrNull()?.size ?: 0
            Log.d("HistoryPagingSource", "Received $responseSize items for page $page, requested ${params.loadSize}")

            LoadResult.Page(
                data = response.getOrThrow(),
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