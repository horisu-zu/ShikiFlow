package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.model.mangadex.manga.MangaData
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMangaDexUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
     operator fun invoke(title: String, nativeTitle: String?, malId: Int): Flow<DataResult<List<String>>> = flow {
         try {
             emit(DataResult.Loading)

             val results = coroutineScope {
                 val nativeResult = nativeTitle?.let { async { mangaDexRepository.getMangaList(title = nativeTitle) } }
                 val titleResult = async { mangaDexRepository.getMangaList(title = title) }

                 Pair(nativeResult?.await(), titleResult.await())
             }

             val result = buildList {
                 results.first?.let { addAll(it) }
                 addAll(results.second)
             }.distinctBy { it.id }

             val mangaDexItems = result.filter { item ->
                 item.attributes.malId == malId.toString()
             }.map { it.id }

             Log.d("GetMangaDexUseCase", "Title $title, MAL ID: $malId")
             Log.d("GetMangaDexUseCase", "Items: $mangaDexItems")

             emit(DataResult.Success(mangaDexItems))
         } catch (e: HttpException) {
             emit(DataResult.Error(e.localizedMessage ?: "Network error: ${e.message}"))
         } catch (e: Exception) {
             emit(DataResult.Error("An unexpected error occurred: ${e.message}"))
         }
    }

    operator fun invoke(mangaDexIds: List<String>): Flow<DataResult<List<MangaData>>> = flow {
        try {
            emit(DataResult.Loading)

            val result = mangaDexRepository.getMangaList(ids = mangaDexIds)
            val coversResponse = coroutineScope {
                result.map { mangaData ->
                    val coverId = mangaData.relationships.first { it.type == "cover_art" }.id
                    async {
                        mangaDexRepository.getCover(coverId)
                    }
                }.awaitAll()
            }

            val mangaDataWithCovers = result.map { mangaData ->
                val coverUrl = coversResponse.first { item ->
                    item.coverUrl.contains(mangaData.id)
                }.coverUrl

                MangaData(
                    id = mangaData.id,
                    title = mangaData.attributes.title,
                    status = mangaData.attributes.status,
                    coverUrl = coverUrl
                )
            }
            emit(DataResult.Success(mangaDataWithCovers))
        } catch (e: HttpException) {
            emit(DataResult.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(DataResult.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}