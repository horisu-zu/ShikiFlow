package com.example.shikiflow.presentation.viewmodel.media.reviews

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
class MediaReviewsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _params = MutableStateFlow(ReviewsParams())

    fun setData(mediaId: Int, mediaType: MediaType) {
        _params.update { params ->
            params.copy(
                mediaId = mediaId,
                mediaType = mediaType
            )
        }
    }

    val mediaReviews = _params
        .filter { params ->
            params.mediaId != null && params.mediaType != null
        }
        .distinctUntilChanged()
        .flatMapLatest { params ->
            mediaRepository.getMediaReviews(
                mediaId = params.mediaId!!,
                mediaType = params.mediaType!!
            )
        }.cachedIn(viewModelScope)
}