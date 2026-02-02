package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.anime.AnimeShortData
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import com.example.shikiflow.domain.model.track.manga.MangaShortData
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.track.manga.MangaUserTrack
import kotlinx.coroutines.flow.Flow

interface MediaTracksRepository {

    fun getAnimeTracks(
        status: UserRateStatus,
        userId: String?
    ): Flow<PagingData<AnimeTrack>>

    fun getBrowseTracks(
        userId: String?,
        title: String,
        userRateStatus: UserRateStatus?
    ): Flow<PagingData<AnimeTrack>>

    fun getMangaTracks(status: UserRateStatus, userId: String?): Flow<PagingData<MangaTrack>>

    suspend fun updateAnimeTrack(
        animeTrack: AnimeUserTrack,
        animeShortData: AnimeShortData? = null
    )

    suspend fun updateMangaTrack(mangaTrack: MangaUserTrack, mangaShortData: MangaShortData? = null)
}