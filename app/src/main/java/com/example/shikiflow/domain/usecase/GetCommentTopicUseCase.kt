package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.CommentType
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCommentTopicUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(
        commentId: Int
    ): Flow<Resource<Map<CommentType, List<Comment>>>> = flow {
        try {
            emit(Resource.Loading())

            val originalResponse = commentRepository.getCommentById(commentId)
            val result = mutableMapOf(
                CommentType.OP to listOf(originalResponse)
            )

            val convertedResponse = Converter.parseDescriptionHtml(originalResponse.commentBody)

            val replyIds = Converter.getCommentStringAnnotations(convertedResponse)
            Log.d("GetCommentUseCase", "Replies: $replyIds")

            coroutineScope {
                val repliedToIds = replyIds[CommentType.REPLIED_TO] ?: emptyList()
                if (repliedToIds.isNotEmpty()) {
                    val repliedToComments = repliedToIds.map { commentId ->
                        async { commentRepository.getCommentById(commentId.toInt()) }
                    }.awaitAll()

                    result[CommentType.REPLIED_TO] = repliedToComments
                    Log.d("GetCommentUseCase", "Replied to comments: ${repliedToComments.size}")
                }

                val repliesIds = replyIds[CommentType.REPLIES] ?: emptyList()
                if (repliesIds.isNotEmpty()) {
                    val repliesComments = repliesIds.map { commentId ->
                        async { commentRepository.getCommentById(commentId.toInt()) }
                    }.awaitAll()

                    result[CommentType.REPLIES] = repliesComments
                    Log.d("GetCommentUseCase", "Replies comments: ${repliesComments.size}")
                }
            }

            val sortedResult = result.entries
                .sortedBy { it.key.ordinal }
                .associate { it.key to it.value }

            emit(Resource.Success(sortedResult))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}