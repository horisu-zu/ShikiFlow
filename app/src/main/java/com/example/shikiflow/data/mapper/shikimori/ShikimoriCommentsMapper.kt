package com.example.shikiflow.data.mapper.shikimori

import com.example.shikiflow.data.datasource.dto.comment.ShikimoriCommentItem
import com.example.shikiflow.data.mapper.shikimori.ShikimoriUserMapper.toDomain
import com.example.shikiflow.domain.model.comment.CommentableType
import com.example.shikiflow.domain.model.comment.ShikiComment

object ShikimoriCommentsMapper {
    fun ShikimoriCommentItem.toDomain(): ShikiComment {
        return ShikiComment(
            id = id,
            commentBody = htmlBody,
            markdownBody = body,
            dateTime = createdAt,
            sender = shikiUser.toDomain(),
            isOfftopic = isOfftopic,
        )
    }

    fun CommentableType.toShikiType(): String {
        return when (this) {
            CommentableType.TOPIC -> "Topic"
            CommentableType.CHARACTER -> "Character"
            CommentableType.PERSON -> "Person"
        }
    }
}