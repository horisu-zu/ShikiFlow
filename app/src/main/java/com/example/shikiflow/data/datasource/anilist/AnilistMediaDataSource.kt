package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.example.graphql.anilist.AiringScheduleQuery
import com.example.graphql.anilist.MediaBrowseQuery
import com.example.graphql.anilist.MediaDetailsQuery
import com.example.graphql.anilist.MediaExternalLinksQuery
import com.example.graphql.anilist.MediaFollowingQuery
import com.example.graphql.anilist.MediaRecommendationsQuery
import com.example.graphql.anilist.MediaReviewQuery
import com.example.graphql.anilist.MediaReviewsQuery
import com.example.graphql.anilist.StudioBrowseQuery
import com.example.graphql.anilist.StudioQuery
import com.example.graphql.anilist.type.MediaSort as ALMediaSort
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.data.mapper.anilist.AnilistMediaMapper.toBrowse
import com.example.shikiflow.data.mapper.anilist.AnilistMediaMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistReviewMapper.toDomain
import com.example.shikiflow.data.mapper.common.CountryOfOriginMapper.toDto
import com.example.shikiflow.data.mapper.common.ExternalLinksMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toAnilistFormat
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toAnilistStatus
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toAnilistType
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistBrowseOrder
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistOrder
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistReviewSort
import com.example.shikiflow.data.mapper.common.SeasonMapper.toAnilistSeason
import com.example.shikiflow.data.mapper.common.StudioMapper.toStudio
import com.example.shikiflow.di.annotations.AnilistApollo
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaFollowing
import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.domain.model.review.ReviewShort
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.sort.ReviewType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.studio.Studio
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnilistMediaDataSource @Inject constructor(
    @param:AnilistApollo private val apolloClient: ApolloClient
): MediaDataSource, BaseNetworkRepository() {

    override fun getMediaDetails(
        id: Int?,
        idMal: Int?,
        mediaType: MediaType
    ): Flow<DataResult<MediaDetails>> {
        val detailsQuery = MediaDetailsQuery(
            type = mediaType.toAnilistType(),
            id = Optional.presentIfNotNull(id),
            malId = Optional.presentIfNotNull(idMal)
        )

        val response = apolloClient.query(detailsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()
            .asDataResult { data ->
                val mediaFollowings = PaginatedList(
                    hasNextPage = data.Page?.pageInfo?.hasNextPage == true,
                    entries = data.Page?.mediaList
                        //Filter Current User Media List Entry (API returns it for some reason)
                        ?.filter { it?.mediaFollowingShort?.id != data.Media?.mediaListEntry?.aLRateEntry?.id }
                        ?.mapNotNull { mediaList ->
                            mediaList?.mediaFollowingShort?.toDomain()
                        } ?: emptyList()
                )

                data.Media?.toDomain(mediaFollowings) ?: throw NoSuchElementException("Empty Response")
            }

        return response
    }

    override suspend fun getMediaFollowings(
        page: Int,
        limit: Int,
        mediaId: Int,
        sort: Sort<UserRateType>
    ): Result<List<MediaFollowing>> {
        val mediaFollowingsQuery = MediaFollowingQuery(
            page = page,
            perPage = limit,
            mediaId = mediaId,
            sort = sort.toAnilistOrder()
        )

        val response = apolloClient.query(mediaFollowingsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.toResult().map { data ->
            data.Page
                ?.mediaList
                ?.mapNotNull { mediaList ->
                    mediaList?.mediaFollowingShort?.toDomain()
                } ?: throw NoSuchElementException("Empty Response")
        }
    }

    override suspend fun browseMedia(
        page: Int,
        limit: Int,
        browseOptions: MediaBrowseOptions,
        isRefresh: Boolean
    ): Result<List<BrowseMedia>> {
        if(browseOptions.name?.isBlank() == true) return Result.success(emptyList())

        val browseQuery = MediaBrowseQuery(
            page = page,
            perPage = limit,
            search = Optional.presentIfNotNull(browseOptions.name),
            mediaType = browseOptions.mediaType.toAnilistType(),
            status = Optional.presentIfNotNull(browseOptions.status?.toAnilistStatus()),
            sort = browseOptions.order?.toAnilistBrowseOrder() ?: ALMediaSort.SCORE_DESC,
            format = Optional.presentIfNotNull(browseOptions.format?.toAnilistFormat()),
            score = Optional.presentIfNotNull(browseOptions.score),
            genre = Optional.presentIfNotNull(browseOptions.genre),
            season = Optional.presentIfNotNull(browseOptions.season?.season?.toAnilistSeason()),
            seasonYear = Optional.presentIfNotNull(browseOptions.season?.year),
            countryOfOrigin = Optional.presentIfNotNull(browseOptions.countryOfOrigin?.toDto())
        )

        val response = apolloClient.query(browseQuery)
            .fetchPolicy(
                fetchPolicy = when(isRefresh) {
                    true -> FetchPolicy.NetworkFirst
                    false -> FetchPolicy.CacheFirst
                }
            )
            .execute()

        return response.toResult().map { data ->
            data.Page
                ?.media
                ?.mapNotNull { media ->
                    media?.mediaBrowse?.toBrowse(browseOptions.mediaType)
                } ?: throw NoSuchElementException("Empty Response")
        }
    }

    override fun getAiringAnimes(
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Flow<PagingData<AiringAnime>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    method = { page, limit ->
                        getAiringSchedule(page, limit, onList, airingAtGreater, airingAtLesser)
                    }
                )
            }
        ).flow.map { pagingData ->
            pagingData.filter { airingAnime ->
                if(onList) {
                    airingAnime.data.userRateStatus != null
                } else true
            }
        }
    }

    override suspend fun getAiringSchedule(
        page: Int,
        limit: Int,
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Result<List<AiringAnime>> {
        val airingQuery = AiringScheduleQuery(
            page = page,
            perPage = limit,
            airingAtGreater = airingAtGreater.toInt(),
            airingAtLesser = airingAtLesser.toInt()
        )

        val response = apolloClient.query(airingQuery)
            .execute()
            .toResult().map { data ->
                data
                    .Page
                    ?.airingSchedules
                    ?.filter { airing ->
                        airing?.aLAiringAnimeShort?.media?.isAdult != true
                    }
                    ?.mapNotNull { airing ->
                        airing?.aLAiringAnimeShort?.toDomain()
                    } ?: emptyList()
            }

        return response
    }

    override fun getSimilarMedia(
        mediaType: MediaType,
        mediaId: Int
    ): Flow<PagingData<BrowseMedia>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    method = { page, limit ->
                        loadMediaRecommendations(mediaType, mediaId, page, limit)
                    }
                )
            }
        ).flow
    }

    override suspend fun loadMediaRecommendations(
        mediaType: MediaType,
        mediaId: Int,
        page: Int,
        limit: Int
    ): Result<List<BrowseMedia>> {
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
    ): Result<List<BrowseMedia>> {
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

    override fun getMediaReviews(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<ReviewType>
    ): Flow<PagingData<ReviewShort>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    method = { page, pageSize ->
                        paginatedMediaReviews(mediaId, sort, page, pageSize)
                    }
                )
            }
        ).flow
    }

    suspend fun paginatedMediaReviews(
        mediaId: Int,
        sort: Sort<ReviewType>,
        page: Int,
        limit: Int
    ): Result<List<ReviewShort>> {
        val reviewsQuery = MediaReviewsQuery(
            mediaId = mediaId,
            sort = sort.toAnilistReviewSort(),
            page = page,
            perPage = limit
        )

        val response = apolloClient.query(reviewsQuery).execute()

        return response.toResult().map { data ->
            data.Media
                ?.reviews
                ?.nodes
                ?.mapNotNull { reviewNode ->
                    reviewNode?.aLReviewShort?.toDomain()
                } ?: emptyList()
        }
    }

    override fun getReview(reviewId: Int): Flow<DataResult<Review>> {
        val reviewQuery = MediaReviewQuery(reviewId)

        val response = apolloClient.query(reviewQuery)
            .toFlow()
            .asDataResult { data ->
                data.Review?.aLReview?.toDomain() ?: throw NoSuchElementException("Empty Response")
            }

        return response
    }

    override fun getStudio(studioId: Int): Flow<DataResult<Studio>> {
        val studioQuery = StudioQuery(studioId)

        val response = apolloClient.query(studioQuery)
            .toFlow()
            .asDataResult { data ->
                data.Studio?.aLStudio?.toStudio() ?: throw NoSuchElementException("Empty Response")
            }

        return response
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