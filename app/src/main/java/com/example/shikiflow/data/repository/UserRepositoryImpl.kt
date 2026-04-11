package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.user.UserActivity
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.UserSocial
import com.example.shikiflow.domain.model.user.stats.StudioStat
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

    override fun fetchCurrentUser(): Flow<DataResult<User>> {
        return getSource().fetchCurrentUser()
    }

    override fun getUserHistory(
        userId: Int,
    ): Flow<PagingData<UserActivity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                GenericPagingSource<UserActivity>(
                    method = { page, limit ->
                        getSource().getPaginatedHistory(userId, page, limit)
                    }
                )
            }
        ).flow
    }

    override fun getUserRates(userId: Int): Flow<DataResult<MediaTypeStats<OverviewStats>>> =
        getSource().getUserRates(userId)

    override fun getUserGenres(
        userId: Int
    ): Flow<DataResult<MediaTypeStats<List<TypeStat>>>> = getSource().getUserGenres(userId)

    override fun getUserTags(
        userId: Int
    ): Flow<DataResult<MediaTypeStats<List<TypeStat>>>> = getSource().getUserTags(userId)

    override fun getUserStaff(
        userId: Int
    ): Flow<DataResult<MediaTypeStats<List<StaffStat>>>> = getSource().getUserStaff(userId)

    override fun getUserVoiceActors(
        userId: Int
    ): Flow<DataResult<List<StaffStat>>> = getSource().getUserVoiceActors(userId)

    override fun getUserStudios(
        userId: Int
    ): Flow<DataResult<List<StudioStat>>> = getSource().getUserStudios(userId)

    override fun getUserStatsCategories(
        userId: Int
    ): Flow<DataResult<UserStatsCategories>> = getSource().getUserStatsCategories(userId)

    override fun getUserSocial(
        userId: Int,
        socialCategory: SocialCategory
    ): Flow<PagingData<UserSocial>> = getSource().getUserSocial(userId, socialCategory)

    override fun getUserFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<PagingData<UserFavorite>> = getSource().getUserFavorites(userId, favoriteCategory)

    override suspend fun getMediaRates(
        userId: Int,
        mediaType: MediaType
    ): List<ShortUserMediaRate> = getSource().getMediaRates(userId, mediaType)

    override fun getUsers(nickname: String): Flow<PagingData<Browse.User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                prefetchDistance = 10,
                initialLoadSize = 30
            ),
            pagingSourceFactory = {
                GenericPagingSource<Browse.User>(
                    method = { page, limit ->
                        getSource().getUsersByNickname(page, limit, nickname)
                    }
                )
            }
        ).flow
    }
}