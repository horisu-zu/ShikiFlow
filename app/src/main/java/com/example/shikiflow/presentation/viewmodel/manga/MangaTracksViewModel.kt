package com.example.shikiflow.presentation.viewmodel.manga

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.track.manga.MangaUserTrack
import com.example.shikiflow.domain.repository.MangaTracksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class MangaTracksViewModel @Inject constructor(
    private val mangaTracksRepository: MangaTracksRepository
): ViewModel() {

    private val _pagingDataMap = mutableMapOf<UserRateStatusEnum, Flow<PagingData<MangaTrack>>>()

    fun getMangaTracks(status: UserRateStatusEnum): Flow<PagingData<MangaTrack>> {
        return _pagingDataMap.getOrPut(status) {
            mangaTracksRepository.getMangaTracks(status).cachedIn(viewModelScope)
        }
    }

    fun updateMangaTrack(mangaTrack: MangaUserTrack) {
        viewModelScope.launch {
            mangaTracksRepository.updateMangaTrack(mangaTrack)
        }
    }
}