package com.example.shikiflow.data.mapper.anilist

import android.util.Log
import com.example.graphql.anilist.ToggleLikeMutation
import com.example.graphql.anilist.TopicCommentQuery
import com.example.graphql.anilist.TopicCommentsQuery
import com.example.graphql.anilist.fragment.ALThread
import com.example.graphql.anilist.fragment.ALThreadComment
import com.example.graphql.anilist.fragment.ALThreadCommentWithHeader
import com.example.graphql.anilist.fragment.ALThreadShort
import com.example.graphql.anilist.type.LikeableType as ALLikeableType
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomainUser
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Like
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadShort
import com.example.graphql.anilist.type.ThreadSort as ALThreadSort
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.social.ThreadComment
import kotlin.time.Instant

object AnilistThreadsMapper {
    fun Sort<ThreadType>.toAnilistThreadSort(): ALThreadSort {
        return when(this.type) {
            ThreadType.TITLE -> {
                when(direction) {
                    SortDirection.ASCENDING -> ALThreadSort.TITLE
                    SortDirection.DESCENDING -> ALThreadSort.TITLE_DESC
                }
            }
            ThreadType.CREATED_AT -> {
                when(direction) {
                    SortDirection.ASCENDING -> ALThreadSort.CREATED_AT
                    SortDirection.DESCENDING -> ALThreadSort.CREATED_AT_DESC
                }
            }
            ThreadType.REPLIED_AT -> {
                when(direction) {
                    SortDirection.ASCENDING -> ALThreadSort.REPLIED_AT
                    SortDirection.DESCENDING -> ALThreadSort.REPLIED_AT_DESC
                }
            }
            ThreadType.REPLY_COUNT -> {
                when(direction) {
                    SortDirection.ASCENDING -> ALThreadSort.REPLY_COUNT
                    SortDirection.DESCENDING -> ALThreadSort.REPLY_COUNT_DESC
                }
            }
            ThreadType.VIEW_COUNT -> {
                when(direction) {
                    SortDirection.ASCENDING -> ALThreadSort.VIEW_COUNT
                    SortDirection.DESCENDING -> ALThreadSort.VIEW_COUNT_DESC
                }
            }
        }
    }

    fun ALThread.toDomainThread(): Thread {
        return Thread(
            id = id,
            title = title ?: "",
            body = body ?: "",
            categories = categories?.mapNotNull { it?.name } ?: emptyList(),
            viewCount = viewCount ?: 0,
            likeCount = likeCount,
            replyCount = replyCount ?: 0,
            isLiked = isLiked ?: false,
            createdBy = user?.aLUserShort?.toDomainUser(),
            createdAt = Instant.fromEpochSeconds(createdAt.toLong())
        )
    }

    fun ALThreadShort.toDomainThread(): ThreadShort {
        return ThreadShort(
            id = id,
            title = title ?: "",
            viewCount = viewCount ?: 0,
            replyCount = replyCount ?: 0,
            lastReplyUser = replyUser?.aLUserShort?.toDomainUser(),
            lastRepliedAt = Instant.fromEpochSeconds(repliedAt?.toLong() ?: 0L)
        )
    }

    fun ALThreadComment.toDomain(): ALComment {
        return ALComment(
            id = id,
            commentBody = comment ?: "",
            markdownBody = markdownBody ?: "",
            dateTime = Instant.fromEpochSeconds(epochSeconds = createdAt.toLong()),
            sender = user?.aLUserShort?.toDomainUser(),
            childComments = childComments.parseChildComments().map { it.toDomain() },
            likesCount = likeCount,
            isLiked = isLiked ?: false
        )
    }

    fun TopicCommentsQuery.ThreadComment.toDomain(): ALComment {
        return ALComment(
            id = id,
            commentBody = comment ?: "",
            markdownBody = markdownBody ?: "",
            dateTime = Instant.fromEpochSeconds(epochSeconds = createdAt.toLong()),
            sender = User(
                id = user?.id ?: 0,
                nickname = user?.name ?: "",
                avatarUrl = user?.avatar?.large ?: "",
                profileBannerUrl = user?.bannerImage
            ),
            childComments = childComments.parseChildComments().map { it.toDomain() },
            likesCount = likeCount,
            isLiked = isLiked ?: false
        )
    }

    fun TopicCommentQuery.ThreadComment.toDomain(): ALComment {
        return ALComment(
            id = id,
            commentBody = comment ?: "",
            markdownBody = markdownBody ?: "",
            dateTime = Instant.fromEpochSeconds(epochSeconds = createdAt.toLong()),
            sender = User(
                id = user?.id ?: 0,
                nickname = user?.name ?: "",
                avatarUrl = user?.avatar?.large ?: "",
                profileBannerUrl = user?.bannerImage
            ),
            childComments = childComments.parseChildComments().map { it.toDomain() },
            likesCount = likeCount,
            isLiked = isLiked ?: false
        )
    }

    fun ALThreadCommentWithHeader.toDomain(): ThreadComment? {
        return thread?.aLThreadShort?.let { alThread ->
            ThreadComment(
                thread = alThread.toDomainThread(),
                comment = ALComment(
                    id = id,
                    commentBody = comment ?: "",
                    markdownBody = "",
                    dateTime = Instant.fromEpochSeconds(epochSeconds = createdAt.toLong()),
                    sender = user?.aLUserShort?.toDomainUser(),
                    childComments = emptyList(),
                    likesCount = likeCount,
                    isLiked = isLiked ?: false
                )
            )
        }
    }

    //For some reason Anilist API returns not the Comment with the said ID, but the Root Comment
    fun ALComment.findComment(targetId: Int): ALComment? {
        if (id == targetId) return this

        childComments.forEach { child ->
            val targetComment = child.findComment(targetId)
            targetComment?.let { comment ->
                return comment
            }
        }

        return null
    }

    private fun Any?.parseChildComments(): List<TopicCommentsQuery.ThreadComment> {
        if (this == null) return emptyList()

        return try {
            @Suppress("UNCHECKED_CAST")
            val list = this as? List<Map<String, Any?>> ?: return emptyList()

            list.map { map ->
                TopicCommentsQuery.ThreadComment(
                    id = map["id"] as? Int ?: 0,
                    comment = map["comment"] as? String,
                    markdownBody = map["markdownBody"] as? String,
                    createdAt = (map["createdAt"] as? Number)?.toInt() ?: 0,
                    user = (map["user"] as? Map<*, *>)?.let { userMap ->
                        TopicCommentsQuery.User(
                            id = userMap["id"] as? Int ?: 0,
                            name = userMap["name"] as? String ?: "",
                            avatar = (userMap["avatar"] as? Map<*, *>)?.let { avatarMap ->
                                TopicCommentsQuery.Avatar(
                                    large = avatarMap["large"] as? String,
                                    __typename = ""
                                )
                            },
                            bannerImage = userMap["bannerImage"] as? String,
                            __typename = ""
                        )
                    },
                    childComments = map["childComments"],
                    likeCount = (map["likeCount"] as? Number)?.toInt() ?: 0,
                    isLiked = map["isLiked"] as? Boolean ?: false,
                    __typename = ""
                )
            }
        } catch (e: Exception) {
            Log.e("AnilistThreadsMapper", "Error: $e")
            emptyList()
        }
    }

    fun ToggleLikeMutation.ToggleLikeV2.toDomainLike(likeableType: LikeableType): Like {
        val (isLiked, likeCount) = when (likeableType) {
            LikeableType.COMMENT -> onThreadComment?.isLiked to onThreadComment?.likeCount
            LikeableType.THREAD -> onThread?.isLiked to onThread?.likeCount
            LikeableType.ACTIVITY -> onListActivity?.let { it.isLiked to it.likeCount }
                ?: onMessageActivity?.let { it.isLiked to it.likeCount }
                ?: onTextActivity?.let { it.isLiked to it.likeCount }
                ?: (null to null)
            LikeableType.ACTIVITY_REPLY -> onActivityReply?.isLiked to onActivityReply?.likeCount
        }

        return Like(
            isLiked = isLiked ?: false,
            likeCount = likeCount ?: 0
        )
    }

    fun LikeableType.toALType(): ALLikeableType {
        return when (this) {
            LikeableType.COMMENT -> ALLikeableType.THREAD_COMMENT
            LikeableType.THREAD -> ALLikeableType.THREAD
            LikeableType.ACTIVITY -> ALLikeableType.ACTIVITY
            LikeableType.ACTIVITY_REPLY -> ALLikeableType.ACTIVITY_REPLY
        }
    }

    fun ALLikeableType.toDomainType(): LikeableType {
        return when (this) {
            ALLikeableType.THREAD_COMMENT -> LikeableType.COMMENT
            ALLikeableType.THREAD -> LikeableType.THREAD
            ALLikeableType.ACTIVITY -> LikeableType.ACTIVITY
            ALLikeableType.ACTIVITY_REPLY -> LikeableType.ACTIVITY_REPLY
            else -> LikeableType.COMMENT
        }
    }
}