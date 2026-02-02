package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.track.OrderOption
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    suspend fun getMediaDetails(id: Int, mediaType: MediaType): Result<MediaDetails>

    fun browseMedia(
        browseType: BrowseType? = null,
        browseOptions: BrowseOptions
    ): Flow<PagingData<Browse>>

    fun getSimilarMedia(mediaType: MediaType, mediaId: Int): Flow<PagingData<Browse>>

    fun getStudioMedia(
        studioId: Int,
        search: String? = null,
        order: OrderOption,
        onList: Boolean? = null
    ): Flow<PagingData<Browse>>

    suspend fun getExternalLinks(mediaType: MediaType, mediaId: Int): Result<List<ExternalLinkData>>
}