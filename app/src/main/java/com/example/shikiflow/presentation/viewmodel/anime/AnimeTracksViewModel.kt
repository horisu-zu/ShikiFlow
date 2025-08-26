package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.withTransaction
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class AnimeTracksViewModel @Inject constructor(
    private val animeTracksRepository: AnimeTracksRepository
): ViewModel() {

    private val _pagingDataMap = mutableMapOf<UserRateStatusEnum, Flow<PagingData<AnimeTrack>>>()

    fun getAnimeTracks(
        status: UserRateStatusEnum
    ): Flow<PagingData<AnimeTrack>> {
        return _pagingDataMap.getOrPut(status) {
            animeTracksRepository.getAnimeTracks(status).cachedIn(viewModelScope)
        }
    }

    fun updateAnimeTrack(animeTrack: AnimeUserTrack) {
        viewModelScope.launch {
            animeTracksRepository.updateAnimeTrack(animeTrack)
        }
    }
}