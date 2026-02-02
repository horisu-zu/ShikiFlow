package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.repository.MediaTracksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AnimeTracksSearchViewModel @Inject constructor(
    private val mediaTracksRepository: MediaTracksRepository
): ViewModel() {
    private var cachedFlow: Flow<PagingData<AnimeTrack>>? = null
    private var cachedParams: Pair<String, UserRateStatus?>? = null

    fun getPaginatedTracks(
        title: String,
        userId: String?,
        userRateStatus: UserRateStatus?
    ): Flow<PagingData<AnimeTrack>> {
        val currentParams = title to userRateStatus

        if (cachedParams == currentParams && cachedFlow != null) {
            return cachedFlow!!
        }

        val paginatedData = mediaTracksRepository
            .getBrowseTracks(
                userId = userId,
                title = title,
                userRateStatus = userRateStatus
            ).cachedIn(viewModelScope)

        cachedFlow = paginatedData
        cachedParams = currentParams

        return paginatedData
    }
}