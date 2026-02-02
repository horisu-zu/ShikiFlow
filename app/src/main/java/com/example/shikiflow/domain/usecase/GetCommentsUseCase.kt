package com.example.shikiflow.domain.usecase

import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(
        topicId: Int
    ): Flow<Resource<List<Comment>>> = flow {
        emit(Resource.Loading())

        val commentResponse = commentRepository.getComments(topicId, limit = 5)

        commentResponse.fold(
            onSuccess = { result ->
                val sortedResponse = result.sortedBy { it.dateTime }

                emit(Resource.Success(sortedResponse))
            },
            onFailure = { exception ->
                emit(Resource.Error("An unexpected error occurred: ${exception.message}"))
            }
        )
    }
}