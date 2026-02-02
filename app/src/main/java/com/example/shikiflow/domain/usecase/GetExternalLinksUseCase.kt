package com.example.shikiflow.domain.usecase

import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetExternalLinksUseCase @Inject  constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(
        id: Int,
        mediaType: MediaType
    ): Flow<Resource<List<ExternalLinkData>>> = flow {
        emit(Resource.Loading())

        val result = mediaRepository.getExternalLinks(mediaType, id)

        result.fold(
            onSuccess = { links ->
                emit(Resource.Success(links))
            },
            onFailure = { exception ->
                emit(Resource.Error(
                    exception.localizedMessage ?: "Network error: ${exception.message}")
                )
            }
        )
    }
}