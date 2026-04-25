package com.example.shikiflow.domain.usecase

import android.util.Log
import com.example.shikiflow.data.mapper.local.MediaShortMapper.toShortData
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class UpdateMangaProgressUseCase @Inject constructor(
    private val mediaTracksRepository: MediaTracksRepository,
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(malId: Int, chapterNumber: Double) {
        val chapterNum = chapterNumber.toInt()

        mediaTracksRepository.getLocalTrack(malId, MediaType.MANGA)
            .first()
            ?.let { mediaTrack ->
                if(mediaTrack.track.progress >= chapterNum) return

                mediaTracksRepository.saveUserRate(
                    entryId = mediaTrack.track.id,
                    mediaType = MediaType.MANGA,
                    mediaId = mediaTrack.shortData.id,
                    malId = malId,
                    status = if(chapterNum == mediaTrack.shortData.totalCount) UserRateStatus.COMPLETED
                        else UserRateStatus.WATCHING,
                    progress = chapterNum
                ).onEach { result ->
                    when(result) {
                        is DataResult.Loading -> {
                            Log.d("UpdateMangaProgressUseCase", "Sending Update Request...")
                        }
                        is DataResult.Error -> {
                            Log.e("UpdateMangaProgressUseCase", "Error: ${result.message}")
                        }
                        is DataResult.Success -> {
                            Log.d("UpdateMangaProgressUseCase", "Result: ${result.data}")
                        }
                    }
                }.collect()
            } ?: run {
                mediaRepository.getMediaDetails(idMal = malId, mediaType = MediaType.MANGA)
                    .onEach { result ->
                        if(result is DataResult.Success) {
                            mediaTracksRepository.saveUserRate(
                                mediaType = MediaType.MANGA,
                                mediaId = result.data.id,
                                malId = malId,
                                status = if(chapterNum == result.data.totalCount) UserRateStatus.COMPLETED
                                    else UserRateStatus.WATCHING,
                                progress = chapterNum,
                                mediaShortData = result.data.toShortData()
                            ).collect()
                        }
                    }.collect()
            }
    }
}