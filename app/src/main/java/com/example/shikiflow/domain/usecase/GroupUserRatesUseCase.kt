package com.example.shikiflow.domain.usecase

import android.util.Log
import coil3.network.HttpException
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.userrate.ComparisonType
import com.example.shikiflow.domain.model.userrate.MediaComparison
import com.example.shikiflow.domain.model.userrate.ShortUserRate.Companion.getImageUrl
import com.example.shikiflow.domain.model.userrate.ShortUserRate.Companion.getMediaId
import com.example.shikiflow.domain.model.userrate.ShortUserRate.Companion.getMediaTitle
import com.example.shikiflow.domain.model.userrate.ShortUserRateData
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GroupUserRatesUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        currentUserId: String,
        targetUserId: String,
        mediaType: MediaType
    ): Resource<Map<ComparisonType, List<MediaComparison>>> {
        try {
            val (currentUserRates, targetUserRates) = coroutineScope {
                val currentUser = async {
                    when(mediaType) {
                        MediaType.ANIME -> userRepository.getUserAnimeRates(currentUserId.toLong())
                        MediaType.MANGA -> userRepository.getUserMangaRates(currentUserId.toLong())
                    }
                }

                val targetUser = async {
                    when(mediaType) {
                        MediaType.ANIME -> userRepository.getUserAnimeRates(targetUserId.toLong())
                        MediaType.MANGA -> userRepository.getUserMangaRates(targetUserId.toLong())
                    }
                }

                Pair(currentUser.await(), targetUser.await())
            }

            Log.d("GroupUserRatesUseCase", "Current User Rates Size: ${currentUserRates.size}")
            Log.d("GroupUserRatesUseCase", "Target User Rates Size: ${targetUserRates.size}")
            val currentRatesMap = currentUserRates.associateBy { it.getMediaId() }
            val targetRatesMap = targetUserRates.associateBy { it.getMediaId() }

            val allMediaIds = (currentRatesMap.keys + targetRatesMap.keys).distinct()

            val comparisonList = allMediaIds.map { mediaId ->
                val currentUserRate = currentRatesMap[mediaId]
                val targetUserRate = targetRatesMap[mediaId]

                MediaComparison(
                    mediaId = mediaId.toString(),
                    mediaTitle = (currentUserRate ?: targetUserRate)?.getMediaTitle() ?: "Unknown Title",
                    mediaImage = (currentUserRate ?: targetUserRate)?.getImageUrl(),
                    currentUserScore = currentUserRate?.let { userRate ->
                        ShortUserRateData(
                            userScore = userRate.score,
                            status = userRate.status
                        )
                    },
                    targetUserScore = targetUserRate?.let { userRate ->
                        ShortUserRateData(
                            userScore = userRate.score,
                            status = userRate.status
                        )
                    },
                    //comparisonType = comparisonType,
                )
            }

            val result = comparisonList.groupBy { comparison ->
                when {
                    comparison.currentUserScore != null && comparison.targetUserScore != null -> ComparisonType.SHARED
                    comparison.currentUserScore != null -> ComparisonType.CURRENT_USER_ONLY
                    else -> ComparisonType.TARGET_USER_ONLY
                }
            }.toSortedMap(comparator = compareBy { it.ordinal })

            return Resource.Success(result)
        } catch (e: HttpException) {
            return if(e.response.code == 403) {
                Resource.Error(message = "Target User's rates are private")
            } else {
                Resource.Error(e.localizedMessage ?: "Network error: ${e.message}")
            }
        } catch (e: Exception) {
            return Resource.Error("An unexpected error occurred: ${e.message}")
        }
    }
}