package com.example.shikiflow.domain.usecase

import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.tracks.TargetType.Companion.toMediaType
import com.example.shikiflow.domain.model.user.UserFavoritesResponse.Companion.toDomain
import com.example.shikiflow.domain.model.user.UserRateExpanded
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Resource<UserRateExpanded> {
        return try {
            val result = coroutineScope {
                val rates = async { userRepository.getUserRates(userId) }
                val favorites = async { userRepository.getUserFavorites(userId) }

                val userRates = rates.await().groupBy { it.targetType }
                    .mapKeys { it.key.toMediaType() }
                val userFavorites = favorites.await().toDomain().groupBy { it.category }
                UserRateExpanded(userRates, userFavorites)
            }

            Resource.Success(result)
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "Network error: ${e.message}")
        } catch (e: Exception) {
            return Resource.Error("An unexpected error occurred: ${e.message}")
        }
    }
}