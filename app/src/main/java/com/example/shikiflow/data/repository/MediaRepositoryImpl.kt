package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.domain.model.review.ReviewShort
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val anilistDataSource: MediaDataSource,
    private val shikimoriDataSource: MediaDataSource,
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope
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
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000L),
            replay = 1
        )

    override fun getMediaDetails(
        id: Int,
        mediaType: MediaType
    ): Flow<DataResult<MediaDetails>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getMediaDetails(id, mediaType)
        }
    }

    override fun paginatedBrowseMedia(
        browseOptions: MediaBrowseOptions
    ): Flow<PagingData<BrowseMedia>> {
        return withSource(dataSource) { dataSource ->
            dataSource.paginatedBrowseMedia(browseOptions)
        }
    }

    override suspend fun browseMedia(
        page: Int,
        size: Int,
        browseOptions: MediaBrowseOptions
    ): Result<List<BrowseMedia>> {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.browseMedia(page, size, browseOptions)
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
        mediaType: MediaType
    ): Flow<PagingData<ReviewShort>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getMediaReviews(mediaId, mediaType)
        }
    }

    override fun getReview(
        reviewId: Int
    ): Flow<DataResult<Review>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getReview(reviewId)
        }
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