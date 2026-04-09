package com.example.shikiflow.data.datasource.anilist

import androidx.paging.ExperimentalPagingApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.example.graphql.anilist.MediaListCollectionQuery
import com.example.graphql.anilist.MediaListTracksQuery
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toAnilistType
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistOrder
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toAnilistRateStatus
import com.example.shikiflow.data.mapper.local.TracksMapper.toDomain
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.AnilistUtils.toResult
import javax.inject.Inject
import kotlin.Result
import kotlin.collections.flatMap

@OptIn(ExperimentalPagingApi::class)
class AnilistTracksDataSource @Inject constructor(
    private val apolloClient: ApolloClient
): MediaTracksDataSource {
    override suspend fun getMediaTracks(
        page: Int,
        limit: Int,
        mediaType: MediaType,
        userId: Int?,
        status: UserRateStatus?,
        order: Sort<UserRateType>?,
        idsList: List<Int>?
    ): Result<List<MediaTrack>> {
        val query = MediaListTracksQuery(
            type = Optional.present(mediaType.toAnilistType()),
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId),
            status = Optional.presentIfNotNull(status?.toAnilistRateStatus()),
            order = Optional.presentIfNotNull(order?.toAnilistOrder()?.let { listOf(it) }),
            idsIn = Optional.presentIfNotNull(idsList)
        )

        val response = apolloClient.query(query)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.toResult().map { data ->
            data.Page
                ?.mediaList
                ?.let { list ->
                    list.mapNotNull { mediaList ->
                        mediaList?.mediaListShort?.toDomain()
                    }
                } ?: emptyList()
        }
    }

    override suspend fun browseMediaTracks(
        page: Int,
        limit: Int,
        mediaType: MediaType,
        userId: Int,
        title: String,
        userRateStatus: UserRateStatus?
    ): Result<List<MediaTrack>> {
        if(title.isBlank()) {
            return Result.success(emptyList())
        }

        val ratesQuery = MediaListCollectionQuery(
            userId = userId,
            type = mediaType.toAnilistType(),
            status = Optional.presentIfNotNull(userRateStatus?.toAnilistRateStatus())
        )
        val ratesResponse = apolloClient.query(ratesQuery).execute()

        return ratesResponse.toResult().map { data ->
            data.MediaListCollection
                ?.lists
                ?.let { list ->
                    list.flatMap { mediaList ->
                        mediaList?.entries?.mapNotNull { list ->
                            list?.mediaListShort?.toDomain()
                        } ?: emptyList()
                    }
                }?.filter { mediaTrack ->
                    mediaTrack.shortData.name.contains(title, ignoreCase = true) ||
                            mediaTrack.shortData.synonyms?.any { synonym ->
                                synonym.contains(title, ignoreCase = true)
                            } == true
                } ?: emptyList()
        }
    }
}