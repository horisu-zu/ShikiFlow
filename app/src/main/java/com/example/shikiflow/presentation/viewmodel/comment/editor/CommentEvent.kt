package com.example.shikiflow.presentation.viewmodel.comment.editor

import com.example.shikiflow.domain.model.comment.Comment

sealed interface CommentEvent {
    data class Published(val comment: Comment, val parentCommentId: Int?): CommentEvent
    data class Updated(val comment: Comment): CommentEvent
    data class Deleted(val commentId: Int): CommentEvent
}