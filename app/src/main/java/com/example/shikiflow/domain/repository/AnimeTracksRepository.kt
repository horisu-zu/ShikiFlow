package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.anime.AnimeTracksResponse
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import kotlinx.coroutines.flow.Flow

interface AnimeTracksRepository {
    fun getAnimeTracks(status: UserRateStatusEnum): Flow<PagingData<AnimeTrack>>

    suspend fun getAnimeTracks(
        page: Int,
        limit: Int,
        userId: String?,
        status: UserRateStatusEnum?,
        order: UserRateOrderInputType?
    ): Result<AnimeTracksResponse>

    suspend fun updateAnimeTrack(animeTrack: AnimeUserTrack)
}