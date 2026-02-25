package com.example.shikiflow.domain.usecase

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
            /*val responseMap = mangaResponse.volumes.entries
                .sortedBy { (volume, _) ->
                    volume.toFloatOrNull() ?: 0f
                }
                .associateTo(LinkedHashMap()) { (key, volume) ->
                    val chaptersList = volume.chapters.values
                        .sortedBy { (chapterKey, _) ->
                            chapterKey.split("_", "-").first().toFloatOrNull() ?: 0f
                        }
                        .flatMap { chapter ->
                            listOf(chapter.id) + chapter.others
                        }

                    key to chaptersList
                }*/

            val chaptersMap = mangaResponse.volumes
                .flatMap { volume ->
                    volume.chapters
                }
                .groupBy { volume ->
                    volume.chapter
                }
                .mapValues { (key, chapters) ->
                    chapters.flatMap { chapter -> listOf(chapter.id) + chapter.others }
                }
                .toSortedMap(
                    comparator = compareBy {
                        it.split("-", "–").first().toFloatOrNull() ?: 0f
                    }
                )

            emit(Resource.Success(chaptersMap))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}