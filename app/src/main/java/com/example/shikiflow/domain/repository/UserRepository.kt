package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.UserHistory
import com.example.shikiflow.domain.model.user.OverviewStats
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.user.MediaTypeStats
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchCurrentUser(): User?

    fun getUserHistory(userId: Int): Flow<PagingData<UserHistory>>

    fun getUserRates(userId: Int): Flow<DataResult<MediaTypeStats<OverviewStats>>>

    fun getUserStatsCategories(userId: Int): Flow<DataResult<UserStatsCategories>>

    fun getUserFavorites(userId: Int, favoriteCategory: FavoriteCategory): Flow<PagingData<UserFavorite>>

    suspend fun getMediaRates(userId: Int, mediaType: MediaType): List<ShortUserMediaRate>

    fun getUsers(
        nickname: String
    ): Flow<PagingData<User>>

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