package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
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
import com.example.shikiflow.domain.model.track.OrderOption
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnilistMediaDetailsDataSource @Inject constructor(
    private val apolloClient: ApolloClient
): MediaDetailsDataSource {

    override suspend fun getMediaDetails(
        id: Int,
        mediaType: MediaType
    ): Result<MediaDetails> {
        val detailsQuery = MediaDetailsQuery(mediaType.toAnilistType(), id)

        return try {
            val response = apolloClient.query(detailsQuery).execute()

            val result = response.data
                ?.Media
                ?.toDomain()

            result?.let { mediaDetails ->
                Result.success(mediaDetails)
            } ?: Result.failure(Exception("No Data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
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

        return try {
            val response = apolloClient.query(browseQuery).execute()

            val result = response.data
                ?.Page
                ?.media
                ?.mapNotNull { media ->
                    media?.mediaBrowse?.toBrowse(browseOptions.mediaType)
                }

            result?.let {
                Result.success(result)
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
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

        return try {
            val response = apolloClient.query(recommendationsQuery).execute()

            val result = response.data
                ?.Media
                ?.recommendations
                ?.nodes
                ?.mapNotNull { mediaRecommendation ->
                    mediaRecommendation?.mediaRecommendation?.mediaBrowse?.toBrowse(mediaType)
                }

            result?.let {
                Result.success(result)
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadStudioMedia(
        studioId: Int,
        page: Int,
        limit: Int,
        search: String?,
        order: OrderOption,
        onList: Boolean?
    ): Result<List<Browse>> {
        val studioBrowseQuery = StudioBrowseQuery(
            studioId = studioId,
            page = page,
            perPage = limit,
            sort = Optional.presentIfNotNull(order.toAnilistBrowseOrder()),
            onList = Optional.presentIfNotNull(onList)
        )

        return try {
            val response = apolloClient.query(studioBrowseQuery).execute()

            val result = response.data
                ?.Studio
                ?.media
                ?.nodes
                ?.mapNotNull { node ->
                    node?.mediaBrowse?.toBrowse(mediaType = MediaType.ANIME)
                }

            result?.let {
                Result.success(result)
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getExternalLinks(
        mediaType: MediaType,
        mediaId: Int
    ): Result<List<ExternalLinkData>> {
        val linksQuery = MediaExternalLinksQuery(mediaId)

        return try {
            val response = apolloClient.query(linksQuery).execute()

            val result = response.data
                ?.Media
                ?.externalLinks
                ?.mapNotNull { link ->
                    link?.toDomain()
                }

            result?.let {
                Result.success(result)
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}