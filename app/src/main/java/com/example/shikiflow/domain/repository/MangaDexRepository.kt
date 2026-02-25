package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.mangadex.aggregate.AggregatedManga
import com.example.shikiflow.domain.model.mangadex.chapter.MangaChapter
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.MangaChapterMetadata
import com.example.shikiflow.domain.model.mangadex.cover.MangaCover
import com.example.shikiflow.domain.model.mangadex.manga.Manga
import com.example.shikiflow.domain.model.mangadex.scanlation_group.ScanlationGroup
import com.example.shikiflow.domain.model.mangadex.user.MangaDexUser
import kotlinx.coroutines.flow.Flow

interface MangaDexRepository {
    suspend fun getMangaList(title: String? = null, ids: List<String> = emptyList()): List<Manga>

    suspend fun aggregateManga(mangaId: String): AggregatedManga

    suspend fun getChapterMetadata(chapterId: String): MangaChapterMetadata

    suspend fun getChapter(chapterId: String): MangaChapter

    suspend fun getCover(coverId: String): MangaCover

    suspend fun getScanlationGroup(groupId: String): ScanlationGroup

    suspend fun getUser(userId: String): MangaDexUser

    fun getGroupMangaChapters(
        mangaId: String,
        groupIds: List<String>,
        uploader: String? = null
    ): Flow<PagingData<MangaChapterMetadata>>
}