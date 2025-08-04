package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.Resource
import javax.inject.Inject

class GetMangaDexUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
    suspend operator fun invoke(title: String, malId: String): Resource<String> {
        return try {
            val result = mangaDexRepository.getMangaList(title)
            val mangaDexItem = result.data.find { item ->
                val enTitle = item.attributes.title.en

                //Look, I know how wacky this is, but in some cases, MangaDex doesn't have the
                //same title, let it be en, ja or ja-ro, as in MyAnimeList.
                //I tested and looks like this one works in 100% of cases
                //(ofc I'm not sure if it really does)
                val matchesTitle = !enTitle.contains("colored", ignoreCase = true)
                item.attributes.links.mal == malId && matchesTitle
            }

            when {
                result.data.isEmpty() -> {
                    Log.d("GetMangaDexUseCase", "No manga found with title: $title or MAL ID: $malId")
                    Resource.Error("No manga found with the title: $title")
                }
                mangaDexItem != null -> {
                    Log.d("GetMangaDexUseCase", mangaDexItem.id)
                    Resource.Success(mangaDexItem.id)
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
}