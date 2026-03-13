package com.example.shikiflow.data.datasource

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import kotlinx.coroutines.flow.Flow

interface MediaTracksDataSource {
    fun getAnimeTracks(
        status: UserRateStatus,
        userId: String?
    ): Flow<PagingData<AnimeTrack>>

    fun getBrowseTracks(
        userId: String?,
        title: String,
        userRateStatus: UserRateStatus?
    ): Flow<PagingData<AnimeTrack>>

    suspend fun getAnimeTracks(
        page: Int = 1,
        limit: Int = 50,
        userId: String? = null,
        status: UserRateStatus? = null,
        order: Sort<UserRateType>? = null,
        idsList: List<Int>? = null
    ): Result<List<AnimeTrack>>

    suspend fun browseAnimeTracks(
        page: Int = 1,
        limit: Int = 15,
        userId: String?,
        name: String? = null,
        userStatus: UserRateStatus? = null
    ): Result<List<AnimeTrack>>

    fun getMangaTracks(status: UserRateStatus, userId: String?): Flow<PagingData<MangaTrack>>

    suspend fun getMangaTracks(
        page: Int = 1,
        limit: Int = 50,
        userId: String? = null,
        status: UserRateStatus? = null,
        order: Sort<UserRateType>? = null
    ): Result<List<MangaTrack>>
}