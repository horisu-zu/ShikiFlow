package com.example.shikiflow.presentation.viewmodel.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MediaStaffViewModel @Inject constructor(
    private val staffRepository: StaffRepository
): ViewModel() {

    private var _staffMap = mutableMapOf<Int, Flow<PagingData<StaffShort>>>()

    fun getMediaStaff(
        mediaId: Int,
        mediaType: MediaType,
        //orderOption: OrderOption
    ): Flow<PagingData<StaffShort>> {
        return _staffMap.getOrPut(mediaId) {
            staffRepository.getMediaStaff(mediaId, mediaType).cachedIn(viewModelScope)
        }
    }
}