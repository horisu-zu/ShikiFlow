package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.Shikimori
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.auth.AuthType
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
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    @param:AniList private val anilistDataSource: MediaDataSource,
    @param:Shikimori private val shikimoriDataSource: MediaDataSource,
    settingsRepository: SettingsRepository
): MediaRepository, BaseNetworkRepository() {

    private val dataSource = settingsRepository.authTypeFlow
        .filterNotNull()
        .map { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> shikimoriDataSource
                AuthType.ANILIST -> anilistDataSource
            }
        }
        .distinctUntilChanged()

    override fun getMediaDetails(
        id: Int?,
        idMal: Int?,
        mediaType: MediaType
    ): Flow<DataResult<MediaDetails>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getMediaDetails(id, idMal, mediaType)
        }
    }

    override fun getMediaFollowings(
        mediaId: Int,
        sort: Sort<UserRateType>
    ): Flow<PagingData<MediaFollowing>> = withSource(dataSource) { dataSource ->
        Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    method = { page, limit ->
                        dataSource.getMediaFollowings(page, limit, mediaId, sort)
                    }
                )
            }
        ).flow
    }

    override fun paginatedBrowseMedia(
        browseOptions: MediaBrowseOptions,
        isRefreshing: Boolean
    ): Flow<PagingData<BrowseMedia>> = withSource(dataSource) { dataSource ->
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
                        dataSource.browseMedia(page, limit, browseOptions, isRefreshing)
                    }
                )
            }
        ).flow
    }

    override suspend fun browseMedia(
        page: Int,
        size: Int,
        browseOptions: MediaBrowseOptions,
        isRefreshing: Boolean
    ): Result<List<BrowseMedia>> {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.browseMedia(page, size, browseOptions, isRefreshing)
        }
    }

    override fun getAiringAnimes(
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Flow<PagingData<AiringAnime>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getAiringAnimes(onList, airingAtGreater, airingAtLesser)
        }
    }

    override fun getSimilarMedia(
        mediaType: MediaType,
        mediaId: Int
    ): Flow<PagingData<BrowseMedia>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getSimilarMedia(mediaType, mediaId)
        }
    }

    override fun getStudioMedia(
        studioId: Int,
        search: String?,
        order: SortType?,
        onList: Boolean?
    ): Flow<PagingData<BrowseMedia>> {
        return withSource(dataSource) { dataSource ->
            Pager(
                config = PagingConfig(
                    pageSize = 18,
                    enablePlaceholders = true,
                    prefetchDistance = 9,
                    initialLoadSize = 18
                ),
                pagingSourceFactory = {
                    GenericPagingSource(
                        method = { page, limit ->
                            dataSource.loadStudioMedia(studioId, page, limit, search, order, onList)
                        }
                    )
                }
            ).flow
        }
    }

    override fun getMediaReviews(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<ReviewType>
    ): Flow<PagingData<ReviewShort>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getMediaReviews(mediaId, mediaType, sort)
        }
    }

    override fun getReview(
        reviewId: Int
    ): Flow<DataResult<Review>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getReview(reviewId)
        }
    }

    override fun getStudio(
        studioId: Int
    ): Flow<DataResult<Studio>> = withSource(dataSource) { dataSource ->
        dataSource.getStudio(studioId)
    }

    override suspend fun getExternalLinks(
        mediaType: MediaType,
        mediaId: Int
    ): Result<List<ExternalLinkData>> {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.getExternalLinks(mediaType, mediaId)
        }
    }
}