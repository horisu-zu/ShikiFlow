package com.example.shikiflow.domain.usecase

import coil3.network.HttpException
import com.example.shikiflow.data.common.comment.CommentItem
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {

    operator fun invoke(
        mediaId: String,
        page: Int = 1,
        limit: Int = 30
    ): Flow<Resource<List<CommentItem>>> = flow {
        try {
            emit(Resource.Loading())
            val commentResponse = commentRepository.getComments(mediaId, page, limit)
            emit(Resource.Success(commentResponse))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}