package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.Shikimori
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.domain.repository.StaffRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StaffRepositoryImpl @Inject constructor(
    @AniList private val anilistDataSource: StaffDataSource,
    @Shikimori private val shikimoriDataSource: StaffDataSource,
    private val settingsRepository: SettingsRepository
) : StaffRepository, BaseNetworkRepository() {

    private val dataSource = settingsRepository.authTypeFlow
        .filterNotNull()
        .map { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> shikimoriDataSource
                AuthType.ANILIST -> anilistDataSource
            }
        }
        .distinctUntilChanged()

    override fun getStaffDetails(id: Int): Flow<DataResult<StaffDetails>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getStaffDetails(id)
        }
    }

    override fun getMediaStaff(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<StaffType>
    ): Flow<PagingData<StaffShort>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getMediaStaff(mediaId, mediaType, sort)
        }
    }

    override fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Flow<PagingData<MediaRole>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getStaffMediaRoles(staffId, mediaType, sort)
        }
    }

    override fun getVoiceActorRoles(
        staffId: Int,
        sort: Sort<CharacterType>
    ): Flow<PagingData<MediaRole>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getVoiceActorRoles(staffId, sort)
        }
    }

    override fun searchStaff(
        search: String
    ): Flow<PagingData<Browse.Staff>> {
        return withSource(dataSource) { dataSource ->
            Pager(
                config = PagingConfig(
                    pageSize = 24,
                    enablePlaceholders = true,
                    prefetchDistance = 12,
                    initialLoadSize = 24
                ),
                pagingSourceFactory = {
                    GenericPagingSource(
                        method = { page, limit ->
                            dataSource.searchStaff(page, limit, search)
                        }
                    )
                }
            ).flow
        }
    }
}