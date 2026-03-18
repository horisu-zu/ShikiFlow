package com.example.shikiflow.data.datasource

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface StaffDataSource {
    fun getStaffDetails(staffId: Int): Flow<DataResult<StaffDetails>>

    fun getMediaStaff(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<StaffType>
    ): Flow<PagingData<StaffShort>>

    fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Flow<PagingData<MediaRole>>

    fun getVoiceActorRoles(
        staffId: Int,
        sort: Sort<CharacterType>
    ): Flow<PagingData<MediaRole>>
}