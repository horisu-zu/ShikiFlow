package com.example.shikiflow.domain.usecase

import coil3.network.HttpException
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AggregateMangaUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
    operator fun invoke(mangaDexId: String): Flow<DataResult<Map<String, List<String>>>> = flow {
        try {
            emit(DataResult.Loading)
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

            emit(DataResult.Success(chaptersMap))
        } catch (e: HttpException) {
            emit(DataResult.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(DataResult.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}