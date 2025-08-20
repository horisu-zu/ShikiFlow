package com.example.shikiflow.data.api

import com.example.shikiflow.data.common.comment.CommentItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApi {

    @GET("/api/comments")
    suspend fun getComments(
        @Query("commentable_id") commentableId: Int,
        @Query("commentable_type") commentableType: String = "Topic",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30,
        @Query("desc") sort: Int = 1, // 1 for descending, 0 for ascending
    ): List<CommentItem>

    @GET("/api/comments/{id}")
    suspend fun getCommentById(
        @Path("id") commentId: String
    ): CommentItem
}