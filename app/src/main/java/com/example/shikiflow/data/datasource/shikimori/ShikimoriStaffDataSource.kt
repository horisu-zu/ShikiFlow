package com.example.shikiflow.data.datasource.shikimori

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.toDomain
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.toStaffRole
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.toVoiceActorRole
import com.example.shikiflow.data.remote.PersonApi
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShikimoriStaffDataSource @Inject constructor(
    private val staffApi: PersonApi
): StaffDataSource {
    override suspend fun getStaffDetails(staffId: Int): Result<StaffDetails> {
        return try {
            val response = staffApi.getPersonDetails(staffId.toString())

            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType
    ): Flow<PagingData<MediaRole>> {
        return Pager(config = PagingConfig(pageSize = Int.MAX_VALUE)) {
            object : PagingSource<Int, MediaRole>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaRole> {
                    return try {
                        val response = staffApi.getPersonDetails(staffId.toString())

                        val mediaRoles = response.works?.mapNotNull { work ->
                            when(mediaType) {
                                MediaType.ANIME -> work.anime?.toStaffRole(work.role)
                                MediaType.MANGA -> work.manga?.toStaffRole(work.role)
                            }
                        } ?: emptyList()

                        LoadResult.Page(
                            data = mediaRoles,
                            prevKey = null,
                            nextKey = null
                        )
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }

                override fun getRefreshKey(state: PagingState<Int, MediaRole>): Int? = null
            }
        }.flow
    }

    override fun getVoiceActorRoles(staffId: Int): Flow<PagingData<MediaRole>> {
        return Pager(config = PagingConfig(pageSize = Int.MAX_VALUE)) {
            object : PagingSource<Int, MediaRole>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaRole> {
                    return try {
                        val response = staffApi.getPersonDetails(staffId.toString())

                        val vaRoles = response.roles?.flatMap { role ->
                            role.toVoiceActorRole()
                        } ?: emptyList()

                        LoadResult.Page(
                            data = vaRoles,
                            prevKey = null,
                            nextKey = null
                        )
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }

                override fun getRefreshKey(state: PagingState<Int, MediaRole>): Int? = null
            }
        }.flow
    }
}