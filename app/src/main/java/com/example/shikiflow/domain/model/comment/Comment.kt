package com.example.shikiflow.domain.model.comment

import androidx.compose.ui.util.fastForEach
import com.example.shikiflow.domain.model.user.User
import kotlin.time.Instant

sealed interface Comment {
    val id: Int
    val commentBody: String
    val markdownBody: String
    val dateTime: Instant
    val sender: User?
}

data class ShikiComment(
    override val id: Int,
    override val commentBody: String,
    override val markdownBody: String,
    override val dateTime: Instant,
    override val sender: User?,
    val isOfftopic: Boolean
): Comment

data class ALComment(
    override val id: Int,
    override val commentBody: String,
    override val markdownBody: String,
    override val dateTime: Instant,
    override val sender: User?,
    val childComments: List<ALComment>,
    val likesCount: Int,
    val isLiked: Boolean
): Comment {
    companion object {
        fun ALComment.findComment(commentId: Int): ALComment? {
            if (id == commentId) return this

            childComments.fastForEach { childComment ->
                childComment.findComment(commentId)?.let { return it }
            }

            return null
        }

        fun ALComment.updateComment(
            commentId: Int,
            transform: (ALComment) -> ALComment
        ): ALComment {
            if (id == commentId) return transform(this)

            val updatedChildComments = childComments.map { childComment ->
                childComment.updateComment(commentId, transform)
            }

            return copy(
                childComments = updatedChildComments
            )
        }

        fun ALComment.deleteComment(commentId: Int): ALComment {
            val filteredComments = childComments
                .filterNot { it.id == commentId }
                .map { it.deleteComment(commentId) }

            return copy(childComments = filteredComments)
        }
    }
}
