package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow

interface StaffRepository {
    suspend fun getStaffDetails(id: Int): Result<StaffDetails>

    fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType
    ): Flow<PagingData<MediaRole>>

    fun getVoiceActorRoles(
        staffId: Int
    ): Flow<PagingData<MediaRole>>
}