package com.example.shikiflow.domain.usecase

import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BrowseUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(
        browseType: BrowseType
    ): Flow<DataResult<List<BrowseMedia>>> = flow {
        emit(DataResult.Loading)

        val result = mediaRepository.browseMedia(
            browseOptions = MediaBrowseOptions(
                mediaType = browseType.mediaType,
                order = browseType.sort,
                status = browseType.status,
                season = browseType.season,
                countryOfOrigin = browseType.countryOfOrigin
            )
        )

        result.fold(
            onSuccess = { data ->
                emit(DataResult.Success(data))
            },
            onFailure = { e ->
                emit(DataResult.Error(e.message ?: "Unknown Error"))
            }
        )
    }
}