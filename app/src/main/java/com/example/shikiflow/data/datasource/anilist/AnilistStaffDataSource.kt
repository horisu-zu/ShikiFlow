package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import com.example.graphql.anilist.StaffDetailsQuery
import com.example.graphql.anilist.StaffMediaRolesQuery
import com.example.graphql.anilist.VoiceActorRolesQuery
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.local.source.StaffMediaPagingSource
import com.example.shikiflow.data.local.source.VoiceActorRolesPagingSource
import com.example.shikiflow.data.mapper.anilist.AnilistCharacterMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toStaffMediaRoles
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toAnilistType
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.common.VoiceActorMediaRole
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.AnilistUtils.toResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnilistStaffDataSource @Inject constructor(
    private val apolloClient: ApolloClient
): StaffDataSource {
    override suspend fun getStaffDetails(staffId: Int): Result<StaffDetails> {
        val staffQuery = StaffDetailsQuery(staffId)

        val response = apolloClient.query(staffQuery).execute()

        return response.toResult().map { data ->
            data.Staff?.toDomain() ?: throw NoSuchElementException("Empty Response")
        }
    }

    override fun getStaffMediaRoles(
        staffId: Int,
        mediaType: MediaType
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
                    staffDataSource = this
                )
            }
        ).flow
    }

    suspend fun paginatedStaffMediaRoles(
        page: Int,
        limit: Int,
        staffId: Int,
        mediaType: MediaType
    ): Result<List<MediaRole>> {
        val staffMediaQuery = StaffMediaRolesQuery(
            staffId = staffId,
            page = page,
            perPage = limit,
            mediaType = mediaType.toAnilistType()
        )

        val response = apolloClient.query(staffMediaQuery).execute()

        return response.toResult().map { data ->
            data.Staff
                ?.staffMedia
                ?.aLMediaStaffRoles
                ?.toStaffMediaRoles() ?: emptyList()
        }
    }

    override fun getVoiceActorRoles(staffId: Int): Flow<PagingData<MediaRole>> {
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
                    staffDataSource = this
                )
            }
        ).flow
    }

    suspend fun paginatedVoiceActorRoles(
        page: Int,
        limit: Int,
        staffId: Int,
        //sort: VASort = VASort.FAVORITES
    ): Result<List<VoiceActorMediaRole>> {
        val voiceActorRolesQuery = VoiceActorRolesQuery(
            staffId = staffId,
            page = page,
            perPage = limit
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
}