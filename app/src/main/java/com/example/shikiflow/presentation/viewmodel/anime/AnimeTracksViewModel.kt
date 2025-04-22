package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.withTransaction
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.dao.AnimeTracksDao
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrack
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity
import com.example.shikiflow.data.local.mediator.AnimeTracksMediator
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class AnimeTracksViewModel @Inject constructor(
    private val animeTracksRepository: AnimeTracksRepository,
    private val appRoomDatabase: AppRoomDatabase,
    private val animeTracksDao: AnimeTracksDao
): ViewModel() {

    private val _pagingDataMap = mutableMapOf<UserRateStatusEnum, Flow<PagingData<AnimeTrack>>>()

    fun getAnimeTracks(
        status: UserRateStatusEnum
    ): Flow<PagingData<AnimeTrack>> {
        return _pagingDataMap.getOrPut(status) {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = true,
                    prefetchDistance = 5,
                    initialLoadSize = 20
                ),
                remoteMediator = AnimeTracksMediator(
                    animeTracksRepository = animeTracksRepository,
                    appRoomDatabase = appRoomDatabase,
                    animeTracksDao = animeTracksDao,
                    userRateStatus = status
                ),
                pagingSourceFactory = { animeTracksDao.getTracksByStatus(status.name) }
            ).flow.cachedIn(viewModelScope)
        }
    }

    fun updateAnimeTrack(
        animeTrack: AnimeTrackEntity
    ) {
        viewModelScope.launch {
            appRoomDatabase.withTransaction {
                animeTracksDao.deleteTrack(animeTrack)
                animeTracksDao.insert(animeTrack)
            }
        }
    }
}