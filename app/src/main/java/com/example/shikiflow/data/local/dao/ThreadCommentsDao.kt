package com.example.shikiflow.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.shikiflow.data.local.entity.thread_comment.CommentEntity
import com.example.shikiflow.data.local.entity.thread_comment.ThreadCommentEntity
import com.example.shikiflow.data.local.entity.thread_comment.UserShortEntity

@Dao
interface ThreadCommentsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments: List<ThreadCommentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(users: List<UserShortEntity>)

    @Update
    suspend fun updateComment(comment: ThreadCommentEntity)

    @Query("SELECT * FROM thread_comment WHERE id = :commentId")
    suspend fun getCommentById(commentId: Int): ThreadCommentEntity

    @Query("""
        SELECT * FROM thread_comment 
        WHERE threadId = :threadId AND parentId is NULL 
        ORDER BY id
    """)
    fun getCommentsByThreadId(threadId: Int): PagingSource<Int, CommentEntity>

    @Transaction
    @Query("""
        WITH RECURSIVE subtree(id, threadId, senderId, parentId, commentBody, dateTime, likesCount, isLiked) AS (
        SELECT * FROM thread_comment WHERE id = :commentId
        UNION ALL
            SELECT c.* FROM thread_comment c
            INNER JOIN subtree s ON c.parentId = s.id
        )
        SELECT * FROM subtree
    """)
    suspend fun getSubtree(commentId: Int): List<CommentEntity>

    @Query("DELETE FROM thread_comment WHERE threadId = :threadId")
    suspend fun deleteCommentsByThreadId(threadId: Int)
}