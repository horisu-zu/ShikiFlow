package com.example.shikiflow.domain.usecase

import coil3.network.HttpException
import com.example.shikiflow.domain.model.kodik.KodikAnime
import com.example.shikiflow.domain.repository.KodikRepository
import com.example.shikiflow.presentation.screen.main.details.anime.watch.TranslationFilter
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAnimeTranslationsUseCase @Inject constructor(
    private val kodikRepository: KodikRepository
) {
    operator fun invoke(id: String): Flow<Resource<Map<TranslationFilter, List<KodikAnime>>>> = flow {
        try {
            emit(Resource.Loading())
            val response = kodikRepository.getAnimeTranslations(id)
            val sortedResponse = response.sortedWith(
                compareByDescending<KodikAnime> { it.episodesCount }
                    .thenByDescending { it.updatedAt }
            )

            val mappedResponse = mapOf(
                TranslationFilter.ALL to sortedResponse,
                TranslationFilter.VOICE to sortedResponse.filter { it.translation.type == "voice" },
                TranslationFilter.SUBTITLE to sortedResponse.filter { it.translation.type == "subtitles" }
            )

            emit(Resource.Success(mappedResponse))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}