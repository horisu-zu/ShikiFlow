package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.MediaDetailsDataSource
import com.example.shikiflow.data.local.source.StudioMediaPagingSource
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.track.OrderOption
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class MediaRepositoryImpl @Inject constructor(
    private val anilistDataSource: MediaDetailsDataSource,
    private val shikimoriDataSource: MediaDetailsDataSource,
    private val settingsRepository: SettingsRepository
): MediaRepository {

    private fun getSource(): MediaDetailsDataSource {
        val authType = settingsRepository.authTypeFlow.value

        return when(authType) {
            AuthType.SHIKIMORI -> shikimoriDataSource
            AuthType.ANILIST -> anilistDataSource
        }
    }

    override suspend fun getMediaDetails(
        id: Int,
        mediaType: MediaType
    ): Result<MediaDetails> = getSource().getMediaDetails(id, mediaType)

    override fun paginatedBrowseMedia(
        browseType: BrowseType?,
        browseOptions: BrowseOptions
    ): Flow<PagingData<Browse>> = getSource().paginatedBrowseMedia(browseType, browseOptions)

    override suspend fun browseMedia(
        page: Int,
        size: Int,
        browseOptions: BrowseOptions
    ): Result<List<Browse>> = getSource().browseMedia(page, size,browseOptions)

    override fun getSimilarMedia(
        mediaType: MediaType,
        mediaId: Int
    ): Flow<PagingData<Browse>> {
        return getSource().getSimilarMedia(mediaType, mediaId)
    }

    override fun getStudioMedia(
        studioId: Int,
        search: String?,
        order: OrderOption,
        onList: Boolean?
    ): Flow<PagingData<Browse>> {
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

    override suspend fun getExternalLinks(
        mediaType: MediaType,
        mediaId: Int
    ): Result<List<ExternalLinkData>> = getSource().getExternalLinks(mediaType, mediaId)
}