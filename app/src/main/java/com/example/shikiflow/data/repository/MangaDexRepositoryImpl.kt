package com.example.shikiflow.data.repository

import com.example.shikiflow.data.remote.MangaDexApi
import com.example.shikiflow.domain.model.mangadex.scanlation_group.ScanlationGroupResponse
import com.example.shikiflow.domain.model.mangadex.user.MangaDexUserResponse
import com.example.shikiflow.domain.repository.MangaDexRepository
import javax.inject.Inject

class MangaDexRepositoryImpl @Inject constructor(
    private val mangaDexApi: MangaDexApi
): MangaDexRepository {

    override suspend fun getMangaList(title: String?, ids: List<String>)
            = mangaDexApi.getMangaList(title, ids = ids)

    override suspend fun aggregateManga(mangaId: String) = mangaDexApi.aggregateManga(mangaId)

    override suspend fun getChapterMetadata(chapterId: String) = mangaDexApi.getChapterMetadata(chapterId)

    override suspend fun getChapter(chapterId: String) = mangaDexApi.getChapter(chapterId)

    override suspend fun getCover(coverId: String) = mangaDexApi.getCover(coverId)

    override suspend fun getScanlationGroup(groupId: String): ScanlationGroupResponse
        = mangaDexApi.getScanlationGroup(groupId)

    override suspend fun getUser(userId: String): MangaDexUserResponse = mangaDexApi.getUser(userId)
}