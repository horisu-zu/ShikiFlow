package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoadChapterUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
    operator fun invoke(
        mangaDexChapterId: String,
        isDataSaver: Boolean
    ): Flow<DataResult<List<String>>> = flow {
        try {
            emit(DataResult.Loading)
            val response = mangaDexRepository.getChapter(mangaDexChapterId)

            val dataString = if(!isDataSaver) "data" else "data-saver"
            val chapterPages = (if(!isDataSaver) response.data else response.dataSaver)
                .map { response.baseUrl + "/$dataString/${response.hash}/" + it }
            Log.d("DownloadChapterUseCase", "Chapter URLs: $chapterPages")

            emit(DataResult.Success(chapterPages))
        } catch (e: HttpException) {
            emit(DataResult.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(DataResult.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}