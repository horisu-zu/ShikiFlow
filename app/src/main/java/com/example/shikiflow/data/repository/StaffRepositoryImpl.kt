package com.example.shikiflow.data.repository

import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.StaffRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
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

    override fun getStaffDetails(id: Int): Flow<DataResult<StaffDetails>> {
        return getSource().getStaffDetails(id)
    }

    override fun getMediaStaff(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<StaffType>
    ): Flow<PagingData<StaffShort>> = getSource().getMediaStaff(mediaId, mediaType, sort)

    override fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Flow<PagingData<MediaRole>> = getSource().getStaffMediaRoles(staffId, mediaType, sort)

    override fun getVoiceActorRoles(
        staffId: Int,
        sort: Sort<CharacterType>
    ): Flow<PagingData<MediaRole>> = getSource().getVoiceActorRoles(staffId, sort)
}