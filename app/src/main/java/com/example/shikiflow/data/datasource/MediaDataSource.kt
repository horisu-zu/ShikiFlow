package com.example.shikiflow.data.datasource

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

interface MediaDataSource {

    fun getMediaDetails(id: Int, mediaType: MediaType): Flow<DataResult<MediaDetails>>

    fun paginatedBrowseMedia(
        browseOptions: BrowseOptions
    ): Flow<PagingData<Browse>>

    suspend fun browseMedia(
        page: Int,
        limit: Int,
        browseOptions: BrowseOptions
    ): Result<List<Browse>>

    fun getAiringAnimes(
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Flow<PagingData<AiringAnime>>

    suspend fun getAiringSchedule(
        page: Int,
        limit: Int,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Result<List<AiringAnime>>

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
        order: SortType?,
        onList: Boolean?
    ): Result<List<Browse>>

    suspend fun getExternalLinks(mediaType: MediaType, mediaId: Int): Result<List<ExternalLinkData>>
}