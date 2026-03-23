package com.example.shikiflow.presentation.viewmodel.media.similar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SimilarMediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _params = MutableStateFlow(SimilarMediaParams())

    val similarMediaFlow = _params
        .filter { state ->
            state.mediaId != null && state.mediaType != null
        }
        .distinctUntilChanged { old, new ->
            old.mediaId == new.mediaId && !new.isRefreshing
        }
        .flatMapLatest { state ->
            mediaRepository.getSimilarMedia(state.mediaType!!, state.mediaId!!)
        }.cachedIn(viewModelScope)

    fun setMediaParams(mediaId: Int, mediaType: MediaType) {
        _params.update { state ->
            state.copy(
                mediaId = mediaId,
                mediaType = mediaType
            )
        }
    }

    fun onRefresh() {
        _params.update { state ->
            state.copy(
                isRefreshing = true
            )
        }
    }
}