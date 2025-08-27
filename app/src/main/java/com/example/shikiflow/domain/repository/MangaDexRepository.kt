package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.mangadex.aggregate.AggregateResponse
import com.example.shikiflow.domain.model.mangadex.chapter.ChapterResponse
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.ChapterMetadataResponse
import com.example.shikiflow.domain.model.mangadex.cover.CoverResponse
import com.example.shikiflow.domain.model.mangadex.manga.MangaDexResponse

interface MangaDexRepository {
    suspend fun getMangaList(title: String? = null, ids: List<String> = emptyList()): MangaDexResponse
    suspend fun aggregateManga(mangaId: String): AggregateResponse
    suspend fun getChapterMetadata(chapterId: String): ChapterMetadataResponse
    suspend fun getChapter(chapterId: String): ChapterResponse
    suspend fun getCover(coverId: String): CoverResponse
}