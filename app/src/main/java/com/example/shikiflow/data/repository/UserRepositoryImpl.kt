package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.Shikimori
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.user.UserActivity
import com.example.shikiflow.domain.model.user.UserFollow
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.UserSocial
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @param:Shikimori private val shikimoriUserDataSource: UserDataSource,
    @param:AniList private val anilistUserDataSource: UserDataSource,
    settingsRepository: SettingsRepository
): UserRepository, BaseNetworkRepository() {

    private val dataSource = settingsRepository.authTypeFlow
        .filterNotNull()
        .map { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> shikimoriUserDataSource
                AuthType.ANILIST -> anilistUserDataSource
            }
        }
        .distinctUntilChanged()

    override fun fetchCurrentUser(authType: AuthType): Flow<DataResult<User>> {
        return when(authType) {
            AuthType.SHIKIMORI -> shikimoriUserDataSource
            AuthType.ANILIST -> anilistUserDataSource
        }.fetchCurrentUser()
    }

    override fun getUserHistory(
        userId: Int,
    ): Flow<PagingData<UserActivity>> {
        return withSource(dataSource) { dataSource ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = true,
                    prefetchDistance = 5,
                    initialLoadSize = 20
                ),
                pagingSourceFactory = {
                    GenericPagingSource(
                        method = { page, limit ->
                            dataSource.getPaginatedHistory(userId, page, limit)
                        }
                    )
                }
            ).flow
        }
    }

    override fun getUserRates(userId: Int): Flow<DataResult<MediaTypeStats<OverviewStats>>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getUserRates(userId)
        }
    }

    override fun getUserGenres(
        userId: Int
    ): Flow<DataResult<MediaTypeStats<List<TypeStat>>>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getUserGenres(userId)
        }
    }

    override fun getUserTags(
        userId: Int
    ): Flow<DataResult<MediaTypeStats<List<TypeStat>>>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getUserTags(userId)
        }
    }

    override fun getUserStaff(
        userId: Int
    ): Flow<DataResult<MediaTypeStats<List<StaffStat>>>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getUserStaff(userId)
        }
    }

    override fun getUserVoiceActors(
        userId: Int
    ): Flow<DataResult<List<StaffStat>>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getUserVoiceActors(userId)
        }
    }

    override fun getUserStudios(
        userId: Int
    ): Flow<DataResult<List<StudioStat>>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getUserStudios(userId)
        }
    }

    override fun getUserStatsCategories(
        userId: Int
    ): Flow<DataResult<UserStatsCategories>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getUserStatsCategories(userId)
        }
    }

    override fun getUserSocial(
        userId: Int,
        socialCategory: SocialCategory
    ): Flow<PagingData<UserSocial>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getUserSocial(userId, socialCategory)
        }
    }

    override fun getUserFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<PagingData<UserFavorite>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getUserFavorites(userId, favoriteCategory)
        }
    }

    override suspend fun getMediaRates(
        userId: Int,
        mediaType: MediaType
    ): List<ShortUserMediaRate> {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.getMediaRates(userId, mediaType)
        }
    }

    override fun getUsers(nickname: String): Flow<PagingData<Browse.User>> {
        return withSource(dataSource) { dataSource ->
            Pager(
                config = PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    prefetchDistance = 10,
                    initialLoadSize = 30
                ),
                pagingSourceFactory = {
                    GenericPagingSource(
                        method = { page, limit ->
                            dataSource.getUsersByNickname(page, limit, nickname)
                        }
                    )
                }
            ).flow
        }
    }

    override suspend fun toggleFavorite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    ): DataResult<Unit> = withSourceSuspend(dataSource) { dataSource ->
        dataSource.toggleFavorite(animeId, mangaId, characterId, staffId, studioId)
    }

    override suspend fun getFollow(
        userId: Int
    ): DataResult<UserFollow> = withSourceSuspend(dataSource) { dataSource ->
        dataSource.getFollow(userId)
    }

    override suspend fun toggleFollow(
        userId: Int,
        isFollowing: Boolean
    ): DataResult<Boolean> = withSourceSuspend(dataSource) { dataSource ->
        dataSource.toggleFollow(userId, isFollowing)
    }
}