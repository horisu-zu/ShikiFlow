package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.repository.CommentRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CommentPagingSource @Inject constructor(
    private val commentsRepository: CommentRepository,
    private val topicId: Int
): PagingSource<Int, Comment>() {
    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        val page = params.key ?: 1

        val response = commentsRepository.getComments(
            topicId = topicId,
            page = page,
            limit = params.loadSize
        )

        return response.fold(
            onSuccess = { result ->
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (result.size < params.loadSize) null else page + 1

                LoadResult.Page(
                    data = result.take(params.loadSize), //Shikimori API returns loadSize + 1
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            }
        )
    }
}