package com.example.shikiflow.data.remote

import com.example.shikiflow.data.datasource.dto.mangadex.ChaptersListResponse
import com.example.shikiflow.data.datasource.dto.mangadex.aggregate.AggregateResponse
import com.example.shikiflow.data.datasource.dto.mangadex.chapter.ChapterResponse
import com.example.shikiflow.data.datasource.dto.mangadex.chapter_metadata.ChapterMetadataResponse
import com.example.shikiflow.data.datasource.dto.mangadex.cover.CoverResponse
import com.example.shikiflow.data.datasource.dto.mangadex.manga.MangaDexResponse
import com.example.shikiflow.data.datasource.dto.mangadex.scanlation_group.ScanlationGroupResponse
import com.example.shikiflow.data.datasource.dto.mangadex.user.MangaDexUserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

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

    @GET("/chapter")
    suspend fun getGroupChaptersList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("manga") mangaId: String,
        @Query("groups[]") scanlationGroups: List<String> = emptyList(),
        @Query("uploader") uploader: String? = null,
        @QueryMap order: Map<String, String> = mapOf("order[chapter]" to "asc")
    ): ChaptersListResponse

    @GET("user/{id}")
    suspend fun getUser(
        @Path("id") userId: String
    ): MangaDexUserResponse
}