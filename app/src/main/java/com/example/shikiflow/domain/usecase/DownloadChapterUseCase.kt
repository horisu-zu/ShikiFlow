package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloadChapterUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
    operator fun invoke(
        mangaDexChapterId: String,
        isDataSaver: Boolean
    ): Flow<Resource<List<String>>> = flow {
        try {
            emit(Resource.Loading())
            val response = mangaDexRepository.getChapter(mangaDexChapterId)

            if(response.result == "ok") {
                val dataString = if(!isDataSaver) "data" else "data-saver"
                val chapterPages = (if(!isDataSaver) response.chapter.data else response.chapter.dataSaver)
                    .map { response.baseUrl + "/$dataString/${response.chapter.hash}/" + it }
                Log.d("DownloadChapterUseCase", "Chapter URLs: $chapterPages")

                emit(Resource.Success(chapterPages))
            } else {
                emit(Resource.Error("Failed loading chapter with ID: $mangaDexChapterId. " +
                        "Result: ${response.result}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}