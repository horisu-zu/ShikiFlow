package com.example.shikiflow.presentation.viewmodel.manga

import android.util.Log
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
import com.example.shikiflow.data.local.dao.MangaTracksDao
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrack
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackEntity
import com.example.shikiflow.data.local.mediator.MangaTracksMediator
import com.example.shikiflow.domain.repository.MangaTracksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class MangaTracksViewModel @Inject constructor(
    private val mangaTracksRepository: MangaTracksRepository,
    private val appRoomDatabase: AppRoomDatabase,
    private val mangaTracksDao: MangaTracksDao
): ViewModel() {

    private val _pagingDataMap = mutableMapOf<UserRateStatusEnum, Flow<PagingData<MangaTrack>>>()

    fun getMangaTracks(status: UserRateStatusEnum): Flow<PagingData<MangaTrack>> {
        Log.d("MangaTracksViewModel", "getMangaTracks: $status")
        return _pagingDataMap.getOrPut(status) {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = true,
                    prefetchDistance = 5,
                    initialLoadSize = 20
                ),
                remoteMediator = MangaTracksMediator(
                    mangaTracksRepository = mangaTracksRepository,
                    appRoomDatabase = appRoomDatabase,
                    mangaTracksDao = mangaTracksDao,
                    userRateStatus = status
                ),
                pagingSourceFactory = { mangaTracksDao.getTracksByStatus(status.name) }
            ).flow.cachedIn(viewModelScope)
        }
    }

    fun updateMangaTrack(mangaTrack: MangaTrackEntity) {
        viewModelScope.launch {
            appRoomDatabase.withTransaction {
                mangaTracksDao.deleteTrack(mangaTrack)
                mangaTracksDao.insert(mangaTrack)
            }
        }
    }
}