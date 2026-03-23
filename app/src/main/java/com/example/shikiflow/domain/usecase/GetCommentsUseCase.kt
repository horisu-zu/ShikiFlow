package com.example.shikiflow.domain.usecase

import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(
        topicId: Int,
        count: Int?
    ): Flow<DataResult<List<Comment>>> = flow {
        emit(DataResult.Loading)

        val commentResponse = commentRepository.getComments(topicId, limit = count ?: 5)

        commentResponse.fold(
            onSuccess = { result ->
                val sortedResponse = result.sortedBy { it.dateTime }

                emit(DataResult.Success(sortedResponse))
            },
            onFailure = { exception ->
                emit(DataResult.Error("An unexpected error occurred: ${exception.message}"))
            }
        )
    }
}