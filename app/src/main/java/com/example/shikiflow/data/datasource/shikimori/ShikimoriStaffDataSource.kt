package com.example.shikiflow.data.datasource.shikimori

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.example.graphql.shikimori.AnimeStaffQuery
import com.example.graphql.shikimori.MangaStaffQuery
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.toDomain
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.toStaffRole
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.toVoiceActorRole
import com.example.shikiflow.data.remote.PersonApi
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ShikimoriStaffDataSource @Inject constructor(
    private val staffApi: PersonApi,
    private val apolloClient: ApolloClient
): StaffDataSource {
    override fun getStaffDetails(staffId: Int): Flow<DataResult<StaffDetails>> = flow {
        emit(DataResult.Loading)
        try {
            val response = staffApi.getPersonDetails(staffId.toString())
            emit(DataResult.Success(response.toDomain()))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: ""))
        }
    }

    override fun getMediaStaff(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<StaffType>
    ): Flow<PagingData<StaffShort>> {
        return Pager(config = PagingConfig(pageSize = Int.MAX_VALUE)) {
            object : PagingSource<Int, StaffShort>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StaffShort> {
                    val result = getShikiMediaStaff(mediaId, mediaType)

                    return result.fold(
                        onSuccess = { response ->
                            LoadResult.Page(
                                data = response,
                                prevKey = null,
                                nextKey = null
                            )
                        },
                        onFailure = { e ->
                            LoadResult.Error(e)
                        }
                    )
                }

                override fun getRefreshKey(state: PagingState<Int, StaffShort>): Int? = null
            }
        }.flow
    }

    suspend fun getShikiMediaStaff(
        mediaId: Int,
        mediaType: MediaType
    ): Result<List<StaffShort>> {
        return when(mediaType) {
            MediaType.ANIME -> {
                val response = apolloClient.query(AnimeStaffQuery(id = mediaId.toString())).execute()

                response.toResult().map { data ->
                    data.animes.firstOrNull()
                        ?.personRoles
                        ?.map { role ->
                            role.personRoleShort.toDomain()
                        } ?: throw IllegalStateException("No Staff data returned")
                }
            }
            MediaType.MANGA -> {
                val response = apolloClient.query(MangaStaffQuery(id = mediaId.toString())).execute()

                response.toResult().map { data ->
                    data.mangas.firstOrNull()
                        ?.personRoles
                        ?.map { role ->
                            role.personRoleShort.toDomain()
                        } ?: throw IllegalStateException("No Staff data returned")
                }
            }
        }
    }

    override fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
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

    override fun getVoiceActorRoles(
        staffId: Int,
        sort: Sort<CharacterType>
    ): Flow<PagingData<MediaRole>> {
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