package com.example.shikiflow.presentation.viewmodel.staff.media_staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaStaffViewModel @Inject constructor(
    private val staffRepository: StaffRepository
): ViewModel() {

    private val _mediaStaffParams = MutableStateFlow(MediaStaffParams())
    val mediaStaffParams = _mediaStaffParams.asStateFlow()

    val mediaStaffItems = _mediaStaffParams
        .filter { it.mediaId != null }
        .flatMapLatest { params ->
            staffRepository.getMediaStaff(
                mediaId = params.mediaId ?: 0,
                mediaType = params.mediaType,
                sort = params.staffSort
            )
        }
        .cachedIn(viewModelScope)

    fun setParams(mediaId: Int, mediaType: MediaType) {
        _mediaStaffParams.update { state ->
            state.copy(
                mediaId = mediaId,
                mediaType = mediaType
            )
        }
    }

    fun setSort(sort: Sort<StaffType>) {
        _mediaStaffParams.update { state ->
            state.copy(
                staffSort = sort
            )
        }
    }
}