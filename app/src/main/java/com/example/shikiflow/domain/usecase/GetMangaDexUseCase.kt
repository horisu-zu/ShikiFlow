package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.data.mangadex.manga.Data
import com.example.shikiflow.data.mangadex.manga.MangaData
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
    suspend operator fun invoke(title: String, malId: String): Resource<List<String>> {
        return try {
            val result = mangaDexRepository.getMangaList(title)
            val mangaDexItems = result.data.filter { item ->
                //val enTitle = item.attributes.title.en

                //Look, I know how wacky this is, but in some cases, MangaDex doesn't have the
                //same title, let it be en, ja or ja-ro, as in MyAnimeList.
                //I tested and looks like this one works in 100% of cases
                //(ofc I'm not sure if it really does)
                //val matchesTitle = !enTitle.contains("colored", ignoreCase = true)
                item.attributes.links.mal == malId
            }.map { it.id }

            when {
                result.data.isEmpty() -> {
                    Log.d("GetMangaDexUseCase", "No manga found with title: $title or MAL ID: $malId")
                    Resource.Error("No manga found with the title: $title")
                }
                mangaDexItems.isNotEmpty() -> {
                    Log.d("GetMangaDexUseCase", "Items: $mangaDexItems")
                    Resource.Success(mangaDexItems)
                }
                else -> {
                    Log.d("GetMangaDexUseCase", "No manga found with MAL ID: $malId")
                    Resource.Error("No manga found with MAL ID: $malId")
                }
            }
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "Network error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.message}")
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