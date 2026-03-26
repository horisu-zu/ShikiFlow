package com.example.shikiflow.data.datasource

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.UserActivity
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.UserSocial
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    fun fetchCurrentUser(): Flow<DataResult<User>>

    fun getUserHistory(userId: Int): Flow<PagingData<UserActivity>>

    suspend fun getPaginatedHistory(
        userId: Int,
        page: Int?,
        limit: Int?
    ): List<UserActivity>

    fun getUserStatsCategories(userId: Int): Flow<DataResult<UserStatsCategories>>

    fun getUserRates(userId: Int): Flow<DataResult<MediaTypeStats<OverviewStats>>>

    fun getUserGenres(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat>>>>

    fun getUserTags(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat>>>>

    fun getUserStaff(userId: Int): Flow<DataResult<MediaTypeStats<List<StaffStat>>>>

    fun getUserVoiceActors(userId: Int): Flow<DataResult<List<StaffStat>>>

    fun getUserStudios(userId: Int): Flow<DataResult<List<StudioStat>>>

    fun getUserFavorites(userId: Int, favoriteCategory: FavoriteCategory): Flow<PagingData<UserFavorite>>

    fun getUserSocial(userId: Int, socialCategory: SocialCategory): Flow<PagingData<UserSocial>>

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