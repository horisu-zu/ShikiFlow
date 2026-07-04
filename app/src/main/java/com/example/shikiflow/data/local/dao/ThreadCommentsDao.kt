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
import com.example.shikiflow.data.local.entity.thread_comment.ThreadEntity
import com.example.shikiflow.data.local.entity.thread_comment.UserShortEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreadCommentsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments: List<ThreadCommentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserShortEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(users: List<UserShortEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllThreads(threads: List<ThreadEntity>)

    @Update
    suspend fun updateComment(comment: ThreadCommentEntity)

    @Query("SELECT * FROM thread_comment WHERE id = :commentId")
    suspend fun getCommentById(commentId: Int): ThreadCommentEntity?

    @Transaction
    @Query("""
        SELECT * FROM thread_comment 
        WHERE threadId = :threadId AND parentId is NULL 
        ORDER BY id
    """)
    fun getCommentsByThreadId(threadId: Int): PagingSource<Int, CommentEntity>

    @Transaction
    @Query("""
        SELECT * FROM thread_comment 
        WHERE senderId = :senderId AND parentId is NULL 
        ORDER BY id DESC
    """)
    fun getCommentsBySender(senderId: Int): PagingSource<Int, CommentEntity>

    @Transaction
    @Query("""
        SELECT * FROM thread_comment 
        WHERE id IN (:commentsIds)
    """)
    fun getComments(commentsIds: Set<Int>): Flow<List<CommentEntity>>

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

    @Query("DELETE FROM thread_comment WHERE senderId = :senderId")
    suspend fun deleteCommentsBySender(senderId: Int)
}