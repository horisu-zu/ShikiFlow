package com.example.shikiflow.domain.usecase

import android.util.Log
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.viewmodel.user.ProfileStats
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import javax.inject.Inject

class GetUserRatesUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Resource<ProfileStats> {
        return try {
            val result = coroutineScope {
                val userMediaStats = async { userRepository.getUserRates(userId.toInt()) }
                val favoriteCategories = async { userRepository.getFavoriteCategories(userId.toInt()) }

                Log.d("GetUserRatesUseCase", "Favorite Categories: ${favoriteCategories.await()}")
                ProfileStats(userMediaStats.await(), favoriteCategories.await())
            }

            Resource.Success(result)
        } catch (e: HttpException) {
            return Resource.Error(e.localizedMessage ?: "Network error: ${e.message}")
        } catch (e: Exception) {
            return Resource.Error("An unexpected error occurred: ${e.message}")
        }
    }
}