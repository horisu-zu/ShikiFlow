package com.example.shikiflow.data.mapper.anilist

import android.util.Log
import com.example.graphql.anilist.TopicCommentQuery
import com.example.graphql.anilist.TopicCommentsQuery
import com.example.graphql.anilist.fragment.ALThread
import com.example.graphql.anilist.fragment.ALThreadCommentWithHeader
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomain
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Thread
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

    fun ALThread.toDomain(): Thread {
        return Thread(
            id = id,
            title = title,
            body = body,
            categories = categories?.mapNotNull { it?.name } ?: emptyList(),
            viewCount = viewCount ?: 0,
            replyCount = replyCount ?: 0,
            lastReplyUser = replyUser?.aLUserShort?.toDomain(),
            lastRepliedAt = Instant.fromEpochSeconds(repliedAt?.toLong() ?: 0L),
            createdBy = user?.aLUserShort?.toDomain(),
            createdAt = Instant.fromEpochSeconds(createdAt.toLong())
        )
    }
    
    /*fun AnilistThreadHeader.toDomain(): ALThreadHeader {
        return ALThreadHeader(
            id = id,
            commentBody = body ?: "",
            dateTime = Instant.fromEpochSeconds(epochSeconds = createdAt.toLong()),
            sender = user?.aLUserShort?.toDomain(),
            title = title,
            viewCount = viewCount ?: 0,
            replyCount = replyCount ?: 0
        )
    }*/
    
    fun TopicCommentsQuery.ThreadComment.toDomain(): ALComment {
        Log.d("AnilistThreadsMapper", "ChildComments: $childComments")
        Log.d("AnilistThreadsMapper", "Comment: $this")
        return ALComment(
            id = id,
            commentBody = comment ?: "",
            dateTime = Instant.fromEpochSeconds(epochSeconds = createdAt.toLong()),
            sender = User(
                id = user?.id ?: 0,
                avatarUrl = user?.avatar?.medium ?: "",
                nickname = user?.name ?: ""
            ),
            childComments = childComments.parseChildComments().map { it.toDomain() },
            likesCount = likeCount
        )
    }

    fun TopicCommentQuery.ThreadComment.toDomain(): ALComment {
        return ALComment(
            id = id,
            commentBody = comment ?: "",
            dateTime = Instant.fromEpochSeconds(epochSeconds = createdAt.toLong()),
            sender = User(
                id = user?.id ?: 0,
                avatarUrl = user?.avatar?.medium ?: "",
                nickname = user?.name ?: ""
            ),
            childComments = childComments.parseChildComments().map { it.toDomain() },
            likesCount = likeCount
        )
    }

    fun ALThreadCommentWithHeader.toDomain(): ThreadComment? {
        return thread?.aLThread?.let { alThread ->
            ThreadComment(
                thread = alThread.toDomain(),
                comment = ALComment(
                    id = id,
                    commentBody = comment ?: "",
                    dateTime = Instant.fromEpochSeconds(epochSeconds = createdAt.toLong()),
                    sender = user?.aLUserShort?.toDomain(),
                    childComments = emptyList(),
                    likesCount = likeCount
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
                    createdAt = (map["createdAt"] as? Number)?.toInt() ?: 0,
                    user = (map["user"] as? Map<*, *>)?.let { userMap ->
                        TopicCommentsQuery.User(
                            id = userMap["id"] as? Int ?: 0,
                            name = userMap["name"] as? String ?: "",
                            avatar = (userMap["avatar"] as? Map<*, *>)?.let { avatarMap ->
                                TopicCommentsQuery.Avatar(
                                    large = avatarMap["large"] as? String,
                                    medium = avatarMap["medium"] as? String
                                )
                            }
                        )
                    },
                    childComments = map["childComments"],
                    likeCount = (map["likeCount"] as? Number)?.toInt() ?: 0
                )
            }
        } catch (e: Exception) {
            Log.e("AnilistThreadsMapper", "Error: $e")
            emptyList()
        }
    }
}