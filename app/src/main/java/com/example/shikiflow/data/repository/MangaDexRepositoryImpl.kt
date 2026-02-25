package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.local.source.ChaptersPagingSource
import com.example.shikiflow.data.mapper.MangaDexMapper.toDomain
import com.example.shikiflow.data.remote.MangaDexApi
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.MangaChapterMetadata
import com.example.shikiflow.domain.model.mangadex.scanlation_group.ScanlationGroup
import com.example.shikiflow.domain.model.mangadex.user.MangaDexUser
import com.example.shikiflow.domain.repository.MangaDexRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MangaDexRepositoryImpl @Inject constructor(
    private val mangaDexApi: MangaDexApi
): MangaDexRepository {

    override suspend fun getMangaList(title: String?, ids: List<String>)
            = mangaDexApi.getMangaList(title, ids = ids).data.map { it.toDomain() }

    override suspend fun aggregateManga(mangaId: String) = mangaDexApi.aggregateManga(mangaId).toDomain()

    override suspend fun getChapterMetadata(chapterId: String) = mangaDexApi.getChapterMetadata(chapterId).data.toDomain()

    override suspend fun getChapter(chapterId: String) = mangaDexApi.getChapter(chapterId).toDomain()

    override suspend fun getCover(coverId: String) = mangaDexApi.getCover(coverId).toDomain()

    override suspend fun getScanlationGroup(groupId: String): ScanlationGroup
        = mangaDexApi.getScanlationGroup(groupId).toDomain()

    override suspend fun getUser(userId: String): MangaDexUser = mangaDexApi.getUser(userId).toDomain()

    override fun getGroupMangaChapters(
        mangaId: String,
        groupIds: List<String>,
        uploader: String?
    ): Flow<PagingData<MangaChapterMetadata>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = true,
                prefetchDistance = 15,
                initialLoadSize = 50
            ),
            pagingSourceFactory = {
                ChaptersPagingSource(
                    mangaDexApi = mangaDexApi,
                    mangaId = mangaId,
                    groupIds = groupIds,
                    uploader = uploader
                )
            }
        ).flow
    }
}