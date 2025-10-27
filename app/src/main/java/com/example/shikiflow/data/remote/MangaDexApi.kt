package com.example.shikiflow.data.remote

import com.example.shikiflow.domain.model.mangadex.aggregate.AggregateResponse
import com.example.shikiflow.domain.model.mangadex.chapter.ChapterResponse
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.ChapterMetadataResponse
import com.example.shikiflow.domain.model.mangadex.cover.CoverResponse
import com.example.shikiflow.domain.model.mangadex.manga.MangaDexResponse
import com.example.shikiflow.domain.model.mangadex.scanlation_group.ScanlationGroupResponse
import com.example.shikiflow.domain.model.mangadex.user.MangaDexUserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MangaDexApi {

    @GET("/manga")
    suspend fun getMangaList(
        @Query("title") title: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("ids[]") ids: List<String> = emptyList() //[] is an API fault
    ): MangaDexResponse

    @GET("/manga/{id}/aggregate")
    suspend fun aggregateManga(
        @Path("id") mangaId: String
    ): AggregateResponse

    @GET("/chapter/{id}")
    suspend fun getChapterMetadata(
        @Path("id") chapterId: String
    ): ChapterMetadataResponse

    @GET("/at-home/server/{id}")
    suspend fun getChapter(
        @Path("id") chapterId: String
    ): ChapterResponse

    @GET("/cover/{id}")
    suspend fun getCover(
        @Path("id") coverId: String
    ): CoverResponse

    @GET("group/{id}")
    suspend fun getScanlationGroup(
        @Path("id") groupId: String
    ): ScanlationGroupResponse

    @GET("user/{id}")
    suspend fun getUser(
        @Path("id") userId: String
    ): MangaDexUserResponse
}