package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.example.graphql.anilist.MediaBrowseQuery
import com.example.graphql.anilist.MediaDetailsQuery
import com.example.graphql.anilist.MediaExternalLinksQuery
import com.example.graphql.anilist.MediaRecommendationsQuery
import com.example.graphql.anilist.StudioBrowseQuery
import com.example.graphql.anilist.type.MediaSort
import com.example.shikiflow.data.datasource.MediaDetailsDataSource
import com.example.shikiflow.data.local.source.BrowsePagingSource
import com.example.shikiflow.data.local.source.MediaRecommendationsPagingSource
import com.example.shikiflow.data.mapper.anilist.AnilistDetailsMapper.toBrowse
import com.example.shikiflow.data.mapper.anilist.AnilistDetailsMapper.toDomain
import com.example.shikiflow.data.mapper.common.ExternalLinksMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toAnilistFormat
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toAnilistStatus
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toAnilistType
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistBrowseOrder
import com.example.shikiflow.data.mapper.common.SeasonMapper.toAnilistSeason
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnilistMediaDetailsDataSource @Inject constructor(
    private val apolloClient: ApolloClient
): MediaDetailsDataSource, BaseNetworkRepository() {

    override fun getMediaDetails(
        id: Int,
        mediaType: MediaType
    ): Flow<DataResult<MediaDetails>> {
        val detailsQuery = MediaDetailsQuery(mediaType.toAnilistType(), id)

        val response = apolloClient.query(detailsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()
            .asDataResult { data ->
                data.Media?.toDomain() ?: throw NoSuchElementException("Empty Response")
            }

        return response
    }

    override fun paginatedBrowseMedia(
        browseType: BrowseType?,
        browseOptions: BrowseOptions
    ): Flow<PagingData<Browse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                BrowsePagingSource(
                    mediaDetailsDataSource = this,
                    browseType = browseType,
                    options = browseOptions
                )
            }
        ).flow
    }

    override suspend fun browseMedia(
        page: Int,
        limit: Int,
        browseOptions: BrowseOptions
    ): Result<List<Browse>> {
        val browseQuery = MediaBrowseQuery(
            page = page,
            perPage = limit,
            search = Optional.presentIfNotNull(browseOptions.name),
            mediaType = browseOptions.mediaType.toAnilistType(),
            status = Optional.presentIfNotNull(browseOptions.status?.toAnilistStatus()),
            sort = browseOptions.order?.toAnilistBrowseOrder() ?: MediaSort.SCORE_DESC,
            format = Optional.presentIfNotNull(browseOptions.format?.toAnilistFormat()),
            score = Optional.presentIfNotNull(browseOptions.score),
            genre = Optional.presentIfNotNull(browseOptions.genre),
            season = Optional.presentIfNotNull(browseOptions.season?.season?.toAnilistSeason()),
            seasonYear = Optional.presentIfNotNull(browseOptions.season?.year)
        )

        val response = apolloClient.query(browseQuery).execute()

        return response.toResult().map { data ->
            data.Page
                ?.media
                ?.mapNotNull { media ->
                    media?.mediaBrowse?.toBrowse(browseOptions.mediaType)
                } ?: throw NoSuchElementException("Empty Response")
        }
    }

    override fun getSimilarMedia(
        mediaType: MediaType,
        mediaId: Int
    ): Flow<PagingData<Browse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                MediaRecommendationsPagingSource(
                    mediaDetailsDataSource = this,
                    mediaType = mediaType,
                    mediaId = mediaId
                )
            }
        ).flow
    }

    override suspend fun loadMediaRecommendations(
        mediaType: MediaType,
        mediaId: Int,
        page: Int,
        limit: Int
    ): Result<List<Browse>> {
        val recommendationsQuery = MediaRecommendationsQuery(mediaId, page, limit)

        val response = apolloClient.query(recommendationsQuery).execute()

        return response.toResult().map { data ->
            data.Media
                ?.recommendations
                ?.nodes
                ?.mapNotNull { mediaRecommendation ->
                    mediaRecommendation?.mediaRecommendation?.mediaBrowse?.toBrowse(mediaType)
                } ?: throw NoSuchElementException("Empty Response")
        }
    }

    override suspend fun loadStudioMedia(
        studioId: Int,
        page: Int,
        limit: Int,
        search: String?,
        order: SortType?,
        onList: Boolean?
    ): Result<List<Browse>> {
        val studioBrowseQuery = StudioBrowseQuery(
            studioId = studioId,
            page = page,
            perPage = limit,
            sort = Optional.presentIfNotNull(order?.toAnilistBrowseOrder()),
            onList = Optional.presentIfNotNull(onList)
        )

        val response = apolloClient.query(studioBrowseQuery).execute()

        return response.toResult().map { data ->
            data.Studio
                ?.media
                ?.nodes
                ?.mapNotNull { node ->
                    node?.mediaBrowse?.toBrowse(mediaType = MediaType.ANIME)
                } ?: throw NoSuchElementException("Empty Response")
        }
    }

    override suspend fun getExternalLinks(
        mediaType: MediaType,
        mediaId: Int
    ): Result<List<ExternalLinkData>> {
        val linksQuery = MediaExternalLinksQuery(mediaId)

        val response = apolloClient.query(linksQuery).execute()

        return response.toResult().map { data ->
            data.Media
                ?.externalLinks
                ?.mapNotNull { link ->
                    link?.toDomain()
                } ?: throw NoSuchElementException("Empty Response")
        }
    }
}