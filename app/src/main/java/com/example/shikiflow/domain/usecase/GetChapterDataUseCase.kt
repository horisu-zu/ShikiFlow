package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.MangaDexChapterMetadata
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetChapterDataUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
    operator fun invoke(chapterIds: List<String>): Flow<Resource<List<MangaDexChapterMetadata>>> = flow {
        try {
            emit(Resource.Loading())

            val chapters = coroutineScope {
                chapterIds.map { chapterId ->
                    async {
                        val response = mangaDexRepository.getChapterMetadata(chapterId)
                        if (response.result == "ok") {
                            Log.d("GetChapterDataUseCase", "Chapter data for ID: ${response.data}")
                            response.data
                        } else {
                            Log.d("GetChapterDataUseCase", "Failed to fetch chapter data for ID: $chapterId, Result: ${response.result}")
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            emit(Resource.Success(chapters))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}