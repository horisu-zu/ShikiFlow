package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.graphql.shikimori.type.AnimeStatusEnum
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.utils.Converter.formatDate
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class GetOngoingsCalendarUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(limit: Int = 45): Flow<Resource<Map<String, List<Browse>>>> = flow {
        var currentSize = limit
        var currentPage = 1
        val ongoings = mutableListOf<Browse>()

        try {
            emit(Resource.Loading())

            /*while(currentSize == limit) {
                mediaRepository.browseMedia(
                    browseOptions = BrowseOptions(
                        mediaType = MediaType.ANIME,
                        status = MediaStatus.ONGOING,
                        score = 1
                    )
                ).onSuccess { result ->
                    ongoings.addAll(result)
                    currentSize = result.size
                    currentPage++
                }.onFailure { result ->
                    Log.d("GetOngoingsCalendarUseCase", "Error Loading Ongoings: ${result.message}")
                    emit(Resource.Error(result.localizedMessage ?: "Error: ${result.message}"))
                    return@onFailure
                }
            }*/

            val mappedOngoings = ongoings.filter { it.nextEpisodeAt != null }
                .groupBy { item -> item.nextEpisodeAt!!
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date }
                .toSortedMap()
                .map { (date, values) ->
                    formatDate(date) to values.sortedByDescending { it.score }
                }.toMap()

            emit(Resource.Success(mappedOngoings))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}