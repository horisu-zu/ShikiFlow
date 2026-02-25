package com.example.shikiflow.data.datasource

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.track.OrderOption
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow

interface MediaDetailsDataSource {

    suspend fun getMediaDetails(id: Int, mediaType: MediaType): Result<MediaDetails>

    fun paginatedBrowseMedia(
        browseType: BrowseType?,
        browseOptions: BrowseOptions
    ): Flow<PagingData<Browse>>

    suspend fun browseMedia(
        page: Int,
        limit: Int,
        browseOptions: BrowseOptions
    ): Result<List<Browse>>

    fun getSimilarMedia(mediaType: MediaType, mediaId: Int): Flow<PagingData<Browse>>

    suspend fun loadMediaRecommendations(
        mediaType: MediaType,
        mediaId: Int,
        page: Int,
        limit: Int
    ): Result<List<Browse>>

    suspend fun loadStudioMedia(
        studioId: Int,
        page: Int,
        limit: Int,
        search: String?,
        order: OrderOption,
        onList: Boolean?
    ): Result<List<Browse>>

    suspend fun getExternalLinks(mediaType: MediaType, mediaId: Int): Result<List<ExternalLinkData>>
}