package com.example.shikiflow.data.common.comment

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