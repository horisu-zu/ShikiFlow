package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.browse.BrowseMedia
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
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getMediaDetails(
        id: Int? = null,
        idMal: Int? = null,
        mediaType: MediaType
    ): Flow<DataResult<MediaDetails>>

    fun getMediaFollowings(
        mediaId: Int,
        sort: Sort<UserRateType>
    ): Flow<PagingData<MediaFollowing>>

    fun paginatedBrowseMedia(
        browseOptions: MediaBrowseOptions
    ): Flow<PagingData<BrowseMedia>>

    suspend fun browseMedia(
        page: Int = 1,
        size: Int = 24,
        browseOptions: MediaBrowseOptions
    ): Result<List<BrowseMedia>>

    fun getAiringAnimes(
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Flow<PagingData<AiringAnime>>

    fun getSimilarMedia(mediaType: MediaType, mediaId: Int): Flow<PagingData<BrowseMedia>>

    fun getStudioMedia(
        studioId: Int,
        search: String? = null,
        order: SortType? = null,
        onList: Boolean? = null
    ): Flow<PagingData<BrowseMedia>>

    fun getMediaReviews(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<ReviewType>
    ): Flow<PagingData<ReviewShort>>

    fun getReview(reviewId: Int): Flow<DataResult<Review>>

    fun getStudio(studioId: Int): Flow<DataResult<Studio>>

    suspend fun getExternalLinks(mediaType: MediaType, mediaId: Int): Result<List<ExternalLinkData>>
}