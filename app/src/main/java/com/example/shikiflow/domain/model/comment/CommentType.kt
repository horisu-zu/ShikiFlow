package com.example.shikiflow.domain.model.comment

enum class CommentType {
    REPLIED_TO,
    OP,
    REPLIES;

    val displayValue: String
        get() = when(this) {
            REPLIED_TO -> "Replied to"
            OP -> "OP"
            REPLIES -> "Replies"
        }
}