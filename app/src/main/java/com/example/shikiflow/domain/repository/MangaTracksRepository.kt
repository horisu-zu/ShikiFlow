package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.graphql.MangaTracksQuery
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.track.manga.MangaUserTrack
import kotlinx.coroutines.flow.Flow

interface MangaTracksRepository {

    fun getMangaTracks(status: UserRateStatusEnum): Flow<PagingData<MangaTrack>>

    suspend fun getMangaTracks(
        page: Int = 1,
        limit: Int = 50,
        userId: String? = null,
        status: UserRateStatusEnum? = null,
        order: UserRateOrderInputType? = null
    ): Result<List<MangaTracksQuery.UserRate>>

    suspend fun updateMangaTrack(mangaTrack: MangaUserTrack)
}