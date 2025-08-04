package com.example.shikiflow.domain.repository

import com.example.shikiflow.data.api.MangaDexApi
import javax.inject.Inject

class MangaDexRepository @Inject constructor(
    private val mangaDexApi: MangaDexApi
) {

    suspend fun getMangaList(title: String) = mangaDexApi.getMangaList(title)

    suspend fun aggregateManga(mangaId: String) = mangaDexApi.aggregateManga(mangaId)
}