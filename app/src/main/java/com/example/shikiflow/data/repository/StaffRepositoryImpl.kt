package com.example.shikiflow.data.repository

import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.StaffRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class StaffRepositoryImpl @Inject constructor(
    private val anilistDataSource: StaffDataSource,
    private val shikimoriDataSource: StaffDataSource,
    private val settingsRepository: SettingsRepository
) : StaffRepository {

    private fun getSource() = runBlocking {
        when(settingsRepository.authTypeFlow.first()) {
            AuthType.SHIKIMORI -> shikimoriDataSource
            AuthType.ANILIST -> anilistDataSource
        }
    }

    override suspend fun getStaffDetails(id: Int): Result<StaffDetails> {
        return getSource().getStaffDetails(id)
    }

    override fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType
    ): Flow<PagingData<MediaRole>> = getSource().getStaffMediaRoles(staffId, mediaType)

    override fun getVoiceActorRoles(
        staffId: Int
    ): Flow<PagingData<MediaRole>> = getSource().getVoiceActorRoles(staffId)
}