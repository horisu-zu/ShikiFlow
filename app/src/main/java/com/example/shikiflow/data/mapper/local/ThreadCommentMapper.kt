package com.example.shikiflow.data.mapper.local

import com.example.shikiflow.data.local.entity.thread_comment.CommentEntity
import com.example.shikiflow.data.local.entity.thread_comment.ThreadCommentEntity
import com.example.shikiflow.data.local.entity.thread_comment.UserShortEntity
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.user.User

object ThreadCommentMapper {
    fun ALComment.toEntityList(
        threadId: Int,
        parentId: Int? = null
    ): List<ThreadCommentEntity> {
        val root = ThreadCommentEntity(
            id = id,
            threadId = threadId,
            senderId = sender?.id ?: 0,
            parentId = parentId,
            commentBody = commentBody,
            dateTime = dateTime,
            likesCount = likesCount,
            isLiked = isLiked
        )

        return listOf(root) + childComments.flatMap { childComment ->
            childComment.toEntityList(threadId, id)
        }
    }

    fun ALComment.toEntity(
        threadId: Int,
        parentId: Int? = null
    ): ThreadCommentEntity {
        return ThreadCommentEntity(
            id = id,
            threadId = threadId,
            senderId = sender?.id ?: 0,
            parentId = parentId,
            commentBody = commentBody,
            dateTime = dateTime,
            likesCount = likesCount,
            isLiked = isLiked
        )
    }

    fun ALComment.allSenders(): List<UserShortEntity> {
        return listOfNotNull(sender?.toEntity()) + childComments.flatMap { comment ->
            comment.allSenders()
        }.distinct()
    }

    fun List<CommentEntity>.toTree(rootId: Int): ALComment {
        val byParent = groupBy { it.comment.parentId }

        fun build(entity: CommentEntity): ALComment = ALComment(
            id = entity.comment.id,
            commentBody = entity.comment.commentBody,
            dateTime = entity.comment.dateTime,
            sender = entity.sender.toDomainUser(),
            childComments = byParent[entity.comment.id].orEmpty().map(::build),
            likesCount = entity.comment.likesCount,
            isLiked = entity.comment.isLiked
        )

        val root = find { it.comment.id == rootId } ?: error("Comment $rootId not found in subtree")

        return build(root)
    }

    fun UserShortEntity.toDomainUser(): User {
        return User(
            id = id,
            nickname = nickname,
            avatarUrl = avatarUrl,
            profileBannerUrl = bannerUrl
        )
    }

    fun User.toEntity(): UserShortEntity {
        return UserShortEntity(
            id = id,
            nickname = nickname,
            avatarUrl = avatarUrl,
            bannerUrl = profileBannerUrl
        )
    }
}