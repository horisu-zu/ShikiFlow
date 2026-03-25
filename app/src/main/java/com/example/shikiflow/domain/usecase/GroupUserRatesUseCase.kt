package com.example.shikiflow.domain.usecase

import android.util.Log
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.ComparisonType
import com.example.shikiflow.domain.model.user.MediaComparison
import com.example.shikiflow.domain.model.user.ShortUserRateData
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GroupUserRatesUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(
        currentUserId: Int,
        targetUserId: Int,
        mediaType: MediaType
    ): Flow<DataResult<Map<ComparisonType, List<MediaComparison>>>> = flow {
        try {
            emit(DataResult.Loading)

            val (currentUserRates, targetUserRates) = coroutineScope {
                val currentUser = async {
                    userRepository.getMediaRates(currentUserId, mediaType)
                }

                val targetUser = async {
                    userRepository.getMediaRates(targetUserId, mediaType)
                }

                Pair(currentUser.await(), targetUser.await())
            }

            Log.d("GroupUserRatesUseCase", "Current User Rates Size: ${currentUserRates.size}")
            Log.d("GroupUserRatesUseCase", "Target User Rates Size: ${targetUserRates.size}")
            val currentRatesMap = currentUserRates.associateBy { it.id }
            val targetRatesMap = targetUserRates.associateBy { it.id }

            val allMediaIds = (currentRatesMap.keys + targetRatesMap.keys).distinct()

            val comparisonList = allMediaIds.map { mediaId ->
                val currentUserRate = currentRatesMap[mediaId]
                val targetUserRate = targetRatesMap[mediaId]

                MediaComparison(
                    id = mediaId.toString(),
                    title = (currentUserRate ?: targetUserRate)?.title ?: "Unknown Title",
                    imageUrl = (currentUserRate ?: targetUserRate)?.imageUrl,
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
                    }
                )
            }

            val result = comparisonList.groupBy { comparison ->
                when {
                    comparison.currentUserScore != null && comparison.targetUserScore != null -> ComparisonType.SHARED
                    comparison.currentUserScore != null -> ComparisonType.CURRENT_USER_ONLY
                    else -> ComparisonType.TARGET_USER_ONLY
                }
            }.toSortedMap(comparator = compareBy { it.ordinal })

            emit(DataResult.Success(result))
        } catch (e: HttpException) {
            if(e.response()?.code() == 403) {
                emit(DataResult.Error(message = "Target User's rates are private"))
            } else {
                emit(DataResult.Error(e.localizedMessage ?: "Network error: ${e.message}"))
            }
        } catch (e: Exception) {
            emit(DataResult.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}