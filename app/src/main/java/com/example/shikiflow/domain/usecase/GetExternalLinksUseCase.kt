package com.example.shikiflow.domain.usecase

import coil3.network.HttpException
import com.example.shikiflow.data.common.ExternalLink
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.MangaRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetExternalLinksUseCase @Inject  constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository
) {
    operator fun invoke(
        id: String,
        mediaType: MediaType
    ): Flow<Resource<List<ExternalLink>>> = flow {
        try {
            emit(Resource.Loading())
            val links = when (mediaType) {
                MediaType.ANIME -> animeRepository.getExternalLinks(id)
                MediaType.MANGA -> mangaRepository.getExternalLinks(id)
            }
            emit(Resource.Success(links))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}