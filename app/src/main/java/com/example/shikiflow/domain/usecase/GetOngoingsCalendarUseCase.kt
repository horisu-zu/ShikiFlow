package com.example.shikiflow.domain.usecase

import coil3.network.HttpException
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class GetOngoingsCalendarUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(limit: Int = 25): Flow<DataResult<Map<LocalDate, List<Browse>>>> = flow {
        var currentSize = limit
        var currentPage = 1
        val ongoings = mutableListOf<Browse>()

        try {
            emit(DataResult.Loading)

            while(currentSize == limit) {
                mediaRepository.browseMedia(
                    page = currentPage,
                    size = currentSize,
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
                    emit(DataResult.Error(result.message ?: "Error: ${result.message}"))
                    return@onFailure
                }
            }

            val mappedOngoings = ongoings.filter { it.nextEpisodeAt != null }
                .groupBy { item -> item.nextEpisodeAt!!
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date }
                .toSortedMap()
                .map { (date, values) ->
                    date to values.sortedByDescending { it.score }
                }.toMap()

            emit(DataResult.Success(mappedOngoings))
        } catch (e: HttpException) {
            emit(DataResult.Error(e.message ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(DataResult.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}