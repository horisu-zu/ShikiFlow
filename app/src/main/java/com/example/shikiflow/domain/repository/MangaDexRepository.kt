package com.example.shikiflow.domain.repository

import com.example.shikiflow.data.remote.MangaDexApi
import javax.inject.Inject

class MangaDexRepository @Inject constructor(
    private val mangaDexApi: MangaDexApi
) {

    suspend fun getMangaList(title: String? = null, ids: List<String> = emptyList())
        = mangaDexApi.getMangaList(title, ids = ids)

    suspend fun aggregateManga(mangaId: String) = mangaDexApi.aggregateManga(mangaId)

    suspend fun getChapterMetadata(chapterId: String) = mangaDexApi.getChapterMetadata(chapterId)

    suspend fun getChapter(chapterId: String) = mangaDexApi.getChapter(chapterId)

    suspend fun getCover(coverId: String) = mangaDexApi.getCover(coverId)
}