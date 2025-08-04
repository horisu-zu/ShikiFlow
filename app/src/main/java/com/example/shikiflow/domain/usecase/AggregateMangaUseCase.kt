package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AggregateMangaUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
    operator fun invoke(mangaDexId: String): Flow<Resource<Map<String, List<String>>>> = flow {
        try {
            emit(Resource.Loading())
            val mangaResponse = mangaDexRepository.aggregateManga(mangaDexId)
            val chaptersMap = mangaResponse.volumes.values
                .flatMap { it.chapters.values }
                .groupBy { it.chapter }
                .mapValues { (_ , chapters) ->
                    chapters.map { it.id }
                }

            Log.d("AggregateMangaUseCase", "Count of Chapters: ${chaptersMap.size}")
            emit(Resource.Success(chaptersMap))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}