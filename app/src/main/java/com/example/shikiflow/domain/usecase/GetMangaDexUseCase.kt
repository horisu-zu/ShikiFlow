package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.model.mangadex.manga.MangaData
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMangaDexUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
     operator fun invoke(title: String, nativeTitle: String?, malId: Int): Flow<Resource<List<String>>> = flow {
         try {
             emit(Resource.Loading())

             val results = coroutineScope {
                 val nativeResult = nativeTitle?.let { async { mangaDexRepository.getMangaList(title = nativeTitle) } }
                 val titleResult = async { mangaDexRepository.getMangaList(title = title) }

                Pair(nativeResult?.await(), titleResult.await())
            }

            val result = buildList {
                results.first?.data?.let { addAll(it) }
                results.second.data.let { addAll(it) }
            }.distinctBy { it.id }

            val mangaDexItems = result.filter { item ->
                item.attributes.links?.mal == malId.toString()
            }.map { it.id }

            when {
                mangaDexItems.isEmpty() -> {
                    Log.d("GetMangaDexUseCase", "No manga found with the title $title and MAL ID: $malId")
                    emit(Resource.Error("No manga found with the title: $title"))
                }
                else -> {
                    Log.d("GetMangaDexUseCase", "Items: $mangaDexItems")
                    emit(Resource.Success(mangaDexItems))
                }
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
             emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }

    operator fun invoke(mangaDexIds: List<String>): Flow<Resource<List<MangaData>>> = flow {
        try {
            emit(Resource.Loading())
            val result = mangaDexRepository.getMangaList(ids = mangaDexIds)

            val coversResponse = coroutineScope {
                result.data.map { mangaData ->
                    val coverId = mangaData.relationships.first { it.type == "cover_art" }.id
                    async {
                        mangaDexRepository.getCover(coverId)
                    }
                }.awaitAll()
            }

            val mangaDataWithCovers = result.data.map { mangaData ->
                val coverUrl = coversResponse.first { covers ->
                    covers.data.relationships.first { rel -> rel.type == "manga"}.id == mangaData.id
                }.data.attributes.fileName

                MangaData(data = mangaData, coverUrl = coverUrl)
            }
            emit(Resource.Success(mangaDataWithCovers))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}