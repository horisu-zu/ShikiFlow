package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.repository.UserRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserPagingSource @Inject constructor(
    private val userRepository: UserRepository,
    private val query: String
): PagingSource<Int, User>() {

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val page = params.key ?: 1

            if(query.isBlank()) {
                return LoadResult.Page(emptyList(), null, null)
            }

            val response = userRepository.getUsersByNickname(
                page = page,
                limit = params.loadSize,
                nickname = query
            )

            response.fold(
                onSuccess = { data ->
                    LoadResult.Page(
                        data = data,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (data.size < params.loadSize) null else page + 1
                    )
                },
                onFailure = { error ->
                    LoadResult.Error(error)
                }
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}