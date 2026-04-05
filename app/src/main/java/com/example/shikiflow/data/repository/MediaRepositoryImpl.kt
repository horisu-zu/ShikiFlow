package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.data.local.source.StudioMediaPagingSource
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
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class MediaRepositoryImpl @Inject constructor(
    private val anilistDataSource: MediaDataSource,
    private val shikimoriDataSource: MediaDataSource,
    private val settingsRepository: SettingsRepository
): MediaRepository {

    private fun getSource() = runBlocking {
        when(settingsRepository.authTypeFlow.first()) {
            AuthType.SHIKIMORI -> shikimoriDataSource
            AuthType.ANILIST -> anilistDataSource
        }
    }

    override fun getMediaDetails(
        id: Int,
        mediaType: MediaType
    ): Flow<DataResult<MediaDetails>> = getSource().getMediaDetails(id, mediaType)

    override fun paginatedBrowseMedia(
        browseOptions: MediaBrowseOptions
    ): Flow<PagingData<BrowseMedia>> = getSource().paginatedBrowseMedia(browseOptions)

    override suspend fun browseMedia(
        page: Int,
        size: Int,
        browseOptions: MediaBrowseOptions
    ): Result<List<BrowseMedia>> = getSource().browseMedia(page, size, browseOptions)

    override fun getAiringAnimes(
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Flow<PagingData<AiringAnime>> = getSource().getAiringAnimes(onList, airingAtGreater, airingAtLesser)

    override fun getSimilarMedia(
        mediaType: MediaType,
        mediaId: Int
    ): Flow<PagingData<BrowseMedia>> {
        return getSource().getSimilarMedia(mediaType, mediaId)
    }

    override fun getStudioMedia(
        studioId: Int,
        search: String?,
        order: SortType?,
        onList: Boolean?
    ): Flow<PagingData<BrowseMedia>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                StudioMediaPagingSource(
                    mediaDetailsDataSource = getSource(),
                    studioId = studioId,
                    search = search,
                    order = order,
                    onList = onList
                )
            }
        ).flow
    }

    override fun getMediaReviews(
        mediaId: Int,
        mediaType: MediaType
    ): Flow<PagingData<ReviewShort>> = getSource().getMediaReviews(mediaId, mediaType)

    override fun getReview(
        reviewId: Int
    ): Flow<DataResult<Review>> = getSource().getReview(reviewId)

    override suspend fun getExternalLinks(
        mediaType: MediaType,
        mediaId: Int
    ): Result<List<ExternalLinkData>> = getSource().getExternalLinks(mediaType, mediaId)
}