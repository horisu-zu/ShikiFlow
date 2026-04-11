package com.example.shikiflow.data.datasource

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.domain.model.review.ReviewShort
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface MediaDataSource {

    fun getMediaDetails(id: Int, mediaType: MediaType): Flow<DataResult<MediaDetails>>

    fun paginatedBrowseMedia(
        browseOptions: MediaBrowseOptions
    ): Flow<PagingData<BrowseMedia>>

    suspend fun browseMedia(
        page: Int,
        limit: Int,
        browseOptions: MediaBrowseOptions
    ): Result<List<BrowseMedia>>

    fun getAiringAnimes(
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Flow<PagingData<AiringAnime>>

    suspend fun getAiringSchedule(
        page: Int,
        limit: Int,
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Result<List<AiringAnime>>

    fun getSimilarMedia(mediaType: MediaType, mediaId: Int): Flow<PagingData<BrowseMedia>>

    suspend fun loadMediaRecommendations(
        mediaType: MediaType,
        mediaId: Int,
        page: Int,
        limit: Int
    ): Result<List<BrowseMedia>>

    suspend fun loadStudioMedia(
        studioId: Int,
        page: Int,
        limit: Int,
        search: String?,
        order: SortType?,
        onList: Boolean?
    ): Result<List<BrowseMedia>>

    fun getMediaReviews(mediaId: Int, mediaType: MediaType): Flow<PagingData<ReviewShort>>

    fun getReview(reviewId: Int): Flow<DataResult<Review>>

    suspend fun getExternalLinks(mediaType: MediaType, mediaId: Int): Result<List<ExternalLinkData>>
}