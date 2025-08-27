package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.graphql.AnimeTracksQuery
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import kotlinx.coroutines.flow.Flow

interface AnimeTracksRepository {
    fun getAnimeTracks(status: UserRateStatusEnum): Flow<PagingData<AnimeTrack>>

    suspend fun getAnimeTracks(
        page: Int = 1,
        limit: Int = 50,
        userId: String? = null,
        status: UserRateStatusEnum? = null,
        order: UserRateOrderInputType? = null
    ): Result<List<AnimeTracksQuery.UserRate>>

    suspend fun updateAnimeTrack(animeTrack: AnimeUserTrack)
}