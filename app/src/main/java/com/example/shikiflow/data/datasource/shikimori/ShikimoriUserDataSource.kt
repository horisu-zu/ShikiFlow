package com.example.shikiflow.data.datasource.shikimori

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.shikimori.CurrentUserQuery
import com.example.graphql.shikimori.UsersQuery
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.data.local.source.HistoryPagingSource
import com.example.shikiflow.data.local.source.UserPagingSource
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.data.datasource.dto.ShikiCreateRateRequest
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.data.datasource.dto.ShikiUpdateRateRequest
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toShikimoriRateStatus
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.data.mapper.shikimori.ShikimoriRateMapper.toDomain
import com.example.shikiflow.data.mapper.shikimori.ShikimoriUserMapper.mapUserStats
import com.example.shikiflow.data.mapper.shikimori.ShikimoriUserMapper.toDomain
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.user.UserActivity
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.model.user.social.Follower
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.UserSocial
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ShikimoriUserDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val userApi: UserApi
): UserDataSource, BaseNetworkRepository() {
    override fun fetchCurrentUser(): Flow<DataResult<User>> {
        return apolloClient.query(CurrentUserQuery())
            .toFlow()
            .asDataResult { data ->
                data.currentUser?.userShort?.toDomain()
                    ?: throw IllegalStateException("No user data returned")
            }
    }

    override fun getUserHistory(userId: Int): Flow<PagingData<UserActivity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { HistoryPagingSource(this, userId) }
        ).flow
    }

    override suspend fun getPaginatedHistory(
        userId: Int,
        page: Int?,
        limit: Int?
    ): List<UserActivity> {
        return userApi.getUserHistory(
            userId = userId.toLong(),
            page = page,
            limit = limit
        ).map { response ->
            response.toDomain()
        }
    }

    override fun getUserStatsCategories(userId: Int): Flow<DataResult<UserStatsCategories>> = flow {
        emit(DataResult.Loading)

        try {
            val (userRates, favorites, friends) = coroutineScope {
                val userRates = async {
                    userApi.getUserRates(userId = userId.toLong())
                        .map { response -> response.toDomain() }
                        .toDomain()
                }
                val favorites = async { getShikiFavorites(userId) }
                val friends = async { getUserFriends(userId) }

                Triple(userRates.await(), favorites.await(), friends.await())
            }

            val userStatsCategories = mapUserStats(userRates, favorites, friends)

            emit(DataResult.Success(userStatsCategories))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getUserRates(
        userId: Int
    ): Flow<DataResult<MediaTypeStats<OverviewStats>>> = flow {
        emit(DataResult.Loading)

        try {
            val response = userApi.getUserRates(userId = userId.toLong())

            val overviewStats = response
                .map { response -> response.toDomain() }
                .toDomain()

            emit(DataResult.Success(overviewStats))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getUserGenres(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat>>>> {
        TODO("Not Available")
    }

    override fun getUserTags(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat>>>> {
        TODO("Not Available")
    }

    override fun getUserStaff(userId: Int): Flow<DataResult<MediaTypeStats<List<StaffStat>>>> {
        TODO("Not Available")
    }

    override fun getUserVoiceActors(userId: Int): Flow<DataResult<List<StaffStat>>> {
        TODO("Not Available")
    }

    override fun getUserStudios(userId: Int): Flow<DataResult<List<StudioStat>>> {
        TODO("Not Available")
    }

    override fun getUserFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<PagingData<UserFavorite>> {
        return Pager(config = PagingConfig(pageSize = 100)) {
            object : PagingSource<Int, UserFavorite>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserFavorite> {
                    return try {
                        val favorites = getShikiFavorites(userId)
                            .filter { it.favoriteCategory == favoriteCategory }

                        return LoadResult.Page(
                            data = favorites,
                            prevKey = null,
                            nextKey = null
                        )
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }
                override fun getRefreshKey(state: PagingState<Int, UserFavorite>): Int? = null
            }
        }.flow
    }

    override fun getUserSocial(
        userId: Int,
        socialCategory: SocialCategory
    ): Flow<PagingData<UserSocial>> {
        return Pager(config = PagingConfig(pageSize = 100)) {
            object : PagingSource<Int, UserSocial>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserSocial> {
                    return try {
                        val friends = getUserFriends(userId).map { user ->
                            Follower(user)
                        }

                        LoadResult.Page(
                            data = friends,
                            prevKey = null,
                            nextKey = null
                        )
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }
                override fun getRefreshKey(state: PagingState<Int, UserSocial>): Int? = null
            }
        }.flow
    }

    override suspend fun getMediaRates(userId: Int, mediaType: MediaType): List<ShortUserMediaRate> {
        return when(mediaType) {
            MediaType.ANIME -> userApi.getUserAnimeRates(userId.toLong())
            MediaType.MANGA -> userApi.getUserMangaRates(userId.toLong())
        }.map { it.toDomain() }
    }

    private suspend fun getShikiFavorites(
        userId: Int
    ): List<UserFavorite> = userApi.getUserFavorites(userId.toLong()).toDomain()

    private suspend fun getUserFriends(
        userId: Int
    ): List<User> {
        return userApi.getUserFriends(userId.toLong()).map { shikiUser ->
            shikiUser.toDomain()
        }
    }

    override fun getUsers(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                prefetchDistance = 10,
                initialLoadSize = 30
            ),
            pagingSourceFactory = {
                UserPagingSource(
                    userDataSource = this,
                    query = query
                )
            }
        ).flow
    }

    override suspend fun getUsersByNickname(
        page: Int,
        limit: Int,
        nickname: String
    ): Result<List<User>> {
        val query = UsersQuery(
            page = Optional.present(page),
            limit = Optional.present(limit),
            search = Optional.present(nickname)
        )

        val response = apolloClient.query(query).execute()

        return response.toResult().map { data ->
            data.users.map { userData ->
                userData.userShort.toDomain()
            }
        }
    }

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
    ): UserMediaRate {
        return when(entryId) {
            null -> {
                userApi.createUserRate(
                    createRequest = ShikiCreateRateRequest(
                        userId = userId?.toLong()!!,
                        targetId = mediaId.toLong(),
                        status = status.toShikimoriRateStatus().name,
                        targetType = mediaType.name.lowercase().replaceFirstChar { it.uppercase() },
                        episodes = if(mediaType == MediaType.ANIME) progress else null,
                        chapters = if(mediaType == MediaType.MANGA) progress else null,
                        volumes = progressVolumes,
                        rewatches = repeat,
                        score = score
                    )
                )
            }
            else -> {
                userApi.updateUserRate(
                    id = entryId.toLong(),
                    request = ShikiUpdateRateRequest(
                        chapters = if(mediaType == MediaType.MANGA) progress else null,
                        episodes = if(mediaType == MediaType.ANIME) progress else null,
                        volumes = progressVolumes,
                        rewatches = repeat,
                        score = score,
                        status = status.toShikimoriRateStatus().name
                    )
                )
            }
        }.toDomain(mediaType)
    }
}