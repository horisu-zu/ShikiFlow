package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.sort.OrderOption
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow

interface StaffRepository {
    suspend fun getStaffDetails(id: Int): Result<StaffDetails>

    fun getMediaStaff(
        mediaId: Int,
        mediaType: MediaType,
        orderOption: OrderOption? = null
    ): Flow<PagingData<StaffShort>>

    fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType,
        orderOption: OrderOption? = null
    ): Flow<PagingData<MediaRole>>

    fun getVoiceActorRoles(
        staffId: Int,
        orderOption: OrderOption? = null
    ): Flow<PagingData<MediaRole>>
}