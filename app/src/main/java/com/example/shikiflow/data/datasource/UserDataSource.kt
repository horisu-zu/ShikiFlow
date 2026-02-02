package com.example.shikiflow.data.datasource

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.UserHistory
import com.example.shikiflow.domain.model.user.UserRateStats
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    suspend fun fetchCurrentUser(): User?

    fun getUserHistory(userId: Int): Flow<PagingData<UserHistory>>

    suspend fun getPaginatedHistory(
        userId: Int,
        page: Int?,
        limit: Int?
    ): List<UserHistory>

    suspend fun getUserRates(userId: Int): UserRateStats

    suspend fun getFavoriteCategories(userId: Int): List<FavoriteCategory>
    fun getUserFavorites(userId: Int, favoriteCategory: FavoriteCategory): Flow<PagingData<UserFavorite>>

    suspend fun getMediaRates(userId: Int, mediaType: MediaType): List<ShortUserMediaRate>

    fun getUsers(query: String): Flow<PagingData<User>>

    suspend fun getUsersByNickname(
        page: Int,
        limit: Int,
        nickname: String
    ): Result<List<User>>

    suspend fun saveUserRate(
        userId: Int? = null,
        entryId: Int? = null,
        mediaType: MediaType,
        mediaId: Int,
        status: UserRateStatus,
        progress: Int? = null,
        progressVolumes: Int? = null,
        repeat: Int? = null,
        score: Int? = null
    ): UserMediaRate
}