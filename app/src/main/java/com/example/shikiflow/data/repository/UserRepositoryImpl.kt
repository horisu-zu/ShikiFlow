package com.example.shikiflow.data.repository

import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.domain.model.auth.AuthType
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
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImpl @Inject constructor(
    private val shikimoriUserDataSource: UserDataSource,
    private val anilistUserDataSource: UserDataSource,
    private val settingsRepository: SettingsRepository
): UserRepository {

    private fun getSource() = runBlocking {
        when(settingsRepository.authTypeFlow.first()) {
            AuthType.SHIKIMORI -> shikimoriUserDataSource
            AuthType.ANILIST -> anilistUserDataSource
        }
    }

    override suspend fun fetchCurrentUser(): User? {
        return getSource().fetchCurrentUser()
    }

    override fun getUserHistory(
        userId: Int,
    ): Flow<PagingData<UserHistory>> = getSource().getUserHistory(userId)

    override fun getUserRates(userId: Int): Flow<DataResult<MediaTypeStats<OverviewStats>>> =
        getSource().getUserRates(userId)

    override fun getUserStatsCategories(
        userId: Int
    ): Flow<DataResult<UserStatsCategories>> = getSource().getUserStatsCategories(userId)

    override fun getUserFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<PagingData<UserFavorite>> = getSource().getUserFavorites(userId, favoriteCategory)

    override suspend fun getMediaRates(
        userId: Int,
        mediaType: MediaType
    ): List<ShortUserMediaRate> = getSource().getMediaRates(userId, mediaType)

    override fun getUsers(nickname: String): Flow<PagingData<User>> = getSource().getUsers(nickname)

    override suspend fun saveUserRate(
        userId: Int?,
        entryId: Int?,
        mediaType: MediaType,
        mediaId: Int,
        status: UserRateStatus,
        progress: Int?,
        progressVolumes: Int?,
        repeat: Int?,
        score: Int?
    ): UserMediaRate = getSource().saveUserRate(
        userId = userId,
        entryId = entryId,
        mediaType = mediaType,
        mediaId = mediaId,
        status = status,
        progress = progress,
        progressVolumes = progressVolumes,
        repeat = repeat,
        score = score
    )
}