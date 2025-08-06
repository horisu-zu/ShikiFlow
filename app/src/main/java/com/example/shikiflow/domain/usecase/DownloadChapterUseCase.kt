package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.Resource
import javax.inject.Inject

class DownloadChapterUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
    suspend operator fun invoke(
        mangaDexChapterId: String,
        isDataSaver: Boolean
    ): Resource<List<String>> {
        return try {
            val response = mangaDexRepository.getChapter(mangaDexChapterId)

            if(response.result == "ok") {
                val dataString = if(!isDataSaver) "data" else "data-saver"
                val chapterPages = (if(!isDataSaver) response.chapter.data else response.chapter.dataSaver)
                    .map { response.baseUrl + "/$dataString/${response.chapter.hash}/" + it }
                Log.d("DownloadChapterUseCase", "Chapter URLs: $chapterPages")

                Resource.Success(chapterPages)
            } else {
                Resource.Error("Failed loading chapter with ID: $mangaDexChapterId. " +
                        "Result: ${response.result}")
            }
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "Network error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.message}")
        }
    }
}