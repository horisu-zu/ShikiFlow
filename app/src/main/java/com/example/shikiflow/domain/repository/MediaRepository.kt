package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun getMediaDetails(id: Int, mediaType: MediaType): Flow<DataResult<MediaDetails>>

    fun paginatedBrowseMedia(
        browseOptions: BrowseOptions
    ): Flow<PagingData<Browse>>

    suspend fun browseMedia(
        page: Int,
        size: Int,
        browseOptions: BrowseOptions
    ): Result<List<Browse>>

    fun getAiringAnimes(
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Flow<PagingData<AiringAnime>>

    fun getSimilarMedia(mediaType: MediaType, mediaId: Int): Flow<PagingData<Browse>>

    fun getStudioMedia(
        studioId: Int,
        search: String? = null,
        order: SortType? = null,
        onList: Boolean? = null
    ): Flow<PagingData<Browse>>

    suspend fun getExternalLinks(mediaType: MediaType, mediaId: Int): Result<List<ExternalLinkData>>
}