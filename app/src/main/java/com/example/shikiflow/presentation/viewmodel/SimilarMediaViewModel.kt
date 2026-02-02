package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SimilarMediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _pagingRecommendationMap = mutableMapOf<Int, Flow<PagingData<Browse>>>()

    fun getSimilarMedia(
        mediaId: Int,
        mediaType: MediaType
    ): Flow<PagingData<Browse>> {
        return _pagingRecommendationMap.getOrPut(mediaId) {
            mediaRepository.getSimilarMedia(mediaType, mediaId).cachedIn(viewModelScope)
        }
    }
}