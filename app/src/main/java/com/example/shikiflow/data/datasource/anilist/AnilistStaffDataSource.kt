package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.example.graphql.anilist.MediaStaffQuery
import com.example.graphql.anilist.StaffDetailsQuery
import com.example.graphql.anilist.StaffMediaRolesQuery
import com.example.graphql.anilist.StaffSearchQuery
import com.example.graphql.anilist.VoiceActorRolesQuery
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.local.source.MediaStaffPagingSource
import com.example.shikiflow.data.local.source.StaffMediaPagingSource
import com.example.shikiflow.data.local.source.VoiceActorRolesPagingSource
import com.example.shikiflow.data.mapper.anilist.AnilistCharacterMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toStaffMediaRole
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toAnilistType
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistCharacterSort
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistMediaSort
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistStaffSort
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.common.StaffMediaRole
import com.example.shikiflow.domain.model.common.VoiceActorMediaRole
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnilistStaffDataSource @Inject constructor(
    private val apolloClient: ApolloClient
): StaffDataSource, BaseNetworkRepository() {
    override fun getStaffDetails(staffId: Int): Flow<DataResult<StaffDetails>> {
        return apolloClient
            .query(StaffDetailsQuery(staffId))
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()
            .asDataResult { response ->
                response.Staff?.toDomain() ?: throw NoSuchElementException("Empty Response")
            }
    }

    override fun getMediaStaff(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<StaffType>
    ): Flow<PagingData<StaffShort>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                MediaStaffPagingSource(
                    mediaId = mediaId,
                    sort = sort,
                    staffDataSource = this
                )
            }
        ).flow
    }

    suspend fun paginatedMediaStaff(
        mediaId: Int,
        page: Int,
        limit: Int,
        sort: Sort<StaffType>
    ): Result<List<StaffShort>> {
        val mediaStaffQuery = MediaStaffQuery(
            mediaId = mediaId,
            page = page,
            perPage = limit,
            sort = sort.toAnilistStaffSort()
        )

        val response = apolloClient.query(mediaStaffQuery).execute()

        return response.toResult().map { data ->
           data.Media
               ?.staff
               ?.edges
               ?.mapNotNull {
                   it?.aLStaffEdgeShort?.toDomain()
               } ?: emptyList()
        }
    }

    override fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Flow<PagingData<MediaRole>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                StaffMediaPagingSource(
                    staffId = staffId,
                    mediaType = mediaType,
                    sort = sort,
                    staffDataSource = this
                )
            }
        ).flow
    }

    suspend fun paginatedStaffMediaRoles(
        page: Int,
        limit: Int,
        staffId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Result<List<StaffMediaRole>> {
        val staffMediaQuery = StaffMediaRolesQuery(
            staffId = staffId,
            page = page,
            perPage = limit,
            mediaType = mediaType.toAnilistType(),
            sort = sort.toAnilistMediaSort()
        )

        val response = apolloClient.query(staffMediaQuery).execute()

        return response.toResult().map { data ->
            data.Staff
                ?.staffMedia
                ?.edges
                ?.mapNotNull { edge ->
                    edge?.aLMediaStaffRole.toStaffMediaRole()
                } ?: emptyList()
        }
    }

    override fun getVoiceActorRoles(
        staffId: Int,
        sort: Sort<CharacterType>
    ): Flow<PagingData<MediaRole>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                VoiceActorRolesPagingSource(
                    staffId = staffId,
                    sort = sort,
                    staffDataSource = this
                )
            }
        ).flow
    }

    suspend fun paginatedVoiceActorRoles(
        page: Int,
        limit: Int,
        staffId: Int,
        sort: Sort<CharacterType>
    ): Result<List<VoiceActorMediaRole>> {
        val voiceActorRolesQuery = VoiceActorRolesQuery(
            staffId = staffId,
            page = page,
            perPage = limit,
            sort = sort.toAnilistCharacterSort()
        )

        val response = apolloClient.query(voiceActorRolesQuery).execute()

        return response.toResult().map { data ->
            data.Staff
                ?.characters
                ?.edges
                ?.mapNotNull { edge ->
                    val characterShort = edge?.node?.aLCharacterShort?.toDomain()
                    val shortMediaList = edge?.media?.mapNotNull { media ->
                        media?.aLMediaBrowseShort?.toDomain()
                    } ?: emptyList()

                    characterShort?.let {
                        VoiceActorMediaRole(
                            characterShort = characterShort,
                            shortMediaList = shortMediaList
                        )
                    }
                } ?: emptyList()
        }
    }

    override suspend fun searchStaff(
        page: Int,
        limit: Int,
        search: String
    ): Result<List<Browse.Staff>> {
        if(search.isBlank()) {
            return Result.success(emptyList())
        }

        val searchQuery = StaffSearchQuery(page, limit, search)
        val response = apolloClient.query(searchQuery).execute()

        return response.toResult().map { data ->
            data.Page
                ?.staff
                ?.mapNotNull { character ->
                    character?.aLStaffShort?.let { aLStaffShort ->
                        Browse.Staff(
                            data = aLStaffShort.toDomain()
                        )
                    }
                } ?: emptyList()
        }
    }
}