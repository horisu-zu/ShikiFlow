package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.anilist.AnilistUserDataSource
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.UserSocial
import javax.inject.Inject

class SocialPagingSource @Inject constructor(
    private val dataSource: AnilistUserDataSource,
    private val userId: Int,
    private val socialCategory: SocialCategory
): PagingSource<Int, UserSocial>() {

    override fun getRefreshKey(state: PagingState<Int, UserSocial>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserSocial> {
        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        val result = when(socialCategory) {
            SocialCategory.FOLLOWINGS -> dataSource.getUserFollowings(userId, currentPage, pageSize)
            SocialCategory.FOLLOWERS -> dataSource.getUserFollowers(userId, currentPage, pageSize)
            SocialCategory.THREADS -> dataSource.getUserThreads(userId, currentPage, pageSize)
            SocialCategory.COMMENTS -> dataSource.getUserThreadComments(userId, currentPage, pageSize)
        }

        return result.fold(
            onSuccess = { response ->
                val prevKey =  if(currentPage > 1) currentPage - 1 else null
                val nextKey = if(response.size < pageSize) null else currentPage + 1

                LoadResult.Page(
                    data = response,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            },
            onFailure = { error ->
                LoadResult.Error(error)
            }
        )
    }
}