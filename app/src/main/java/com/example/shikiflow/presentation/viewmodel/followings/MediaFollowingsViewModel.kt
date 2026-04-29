package com.example.shikiflow.presentation.viewmodel.followings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaFollowingsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _params = MutableStateFlow(MediaFollowingsParams())
    val params = _params.asStateFlow()

    val mediaFollowings = _params
        .filter { params ->
            params.mediaId != null
        }
        .distinctUntilChanged()
        .flatMapLatest { params ->
            mediaRepository.getMediaFollowings(
                mediaId = params.mediaId!!,
                sort = params.sort
            )
        }
        .cachedIn(viewModelScope)

    fun setMediaId(mediaId: Int) {
        _params.update { params -> params.copy(mediaId = mediaId) }
    }

    fun setSort(sort: Sort<UserRateType>) {
        _params.update { params -> params.copy(sort = sort) }
    }
}