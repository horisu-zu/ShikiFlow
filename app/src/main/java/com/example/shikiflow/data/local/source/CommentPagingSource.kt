package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.domain.model.comment.CommentItem
import com.example.shikiflow.domain.repository.CommentRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CommentPagingSource @Inject constructor(
    private val commentsRepository: CommentRepository,
    private val topicId: String
): PagingSource<Int, CommentItem>() {
    override fun getRefreshKey(state: PagingState<Int, CommentItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentItem> {
        return try {
            val page = params.key ?: 1

            val response = commentsRepository.getComments(
                topicId = topicId,
                page = page,
                limit = params.loadSize
            )

            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (response.isNotEmpty()) page + 1 else null

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