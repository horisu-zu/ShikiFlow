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
import com.example.shikiflow.domain.model.user.UserHistory
import com.example.shikiflow.domain.model.user.UserRateStats
import com.example.shikiflow.data.mapper.shikimori.ShikimoriRateMapper.toDomain
import com.example.shikiflow.data.mapper.shikimori.ShikimoriUserMapper.toDomain
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class ShikimoriUserDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val userApi: UserApi
): UserDataSource {

    private var cachedData: List<UserFavorite>? = null
    private var cachedUserId: Int? = null

    override suspend fun fetchCurrentUser(): User? {
        val response = apolloClient.query(CurrentUserQuery()).execute()

        return response.data?.currentUser?.userShort?.toDomain()
    }

    override fun getUserHistory(userId: Int): Flow<PagingData<UserHistory>> {
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
    ): List<UserHistory> {
        return userApi.getUserHistory(
            userId = userId.toLong(),
            page = page,
            limit = limit
        ).map { response ->
            response.toDomain()
        }
    }

    override suspend fun getUserRates(
        userId: Int
    ): UserRateStats {
        val response = userApi.getUserRates(userId = userId.toLong())

        return response.map { response -> response.toDomain() }
            .toDomain()
    }

    override suspend fun getFavoriteCategories(userId: Int): List<FavoriteCategory> {
        val response = getShikiFavorites(userId)

        return response.map { it.favoriteCategory }.distinct()
    }

    override fun getUserFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<PagingData<UserFavorite>> {
        return Pager(config = PagingConfig(pageSize = 100)) {
            object : PagingSource<Int, UserFavorite>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserFavorite> {
                    val favorites = getShikiFavorites(userId)
                        .filter { it.favoriteCategory == favoriteCategory }

                    //Eww... Well, it's certainly a way of faking data load
                    //(it's better than having a loading indicator appear for an instant)
                    delay(200)

                    return LoadResult.Page(
                        data = favorites,
                        prevKey = null,
                        nextKey = null
                    )
                }
                override fun getRefreshKey(state: PagingState<Int, UserFavorite>): Int? = null
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
    ): List<UserFavorite> {
        return if (cachedUserId == userId && cachedData != null) {
            cachedData!!
        } else {
            val response = userApi.getUserFavorites(userId.toLong()).toDomain()

            cachedData = response
            cachedUserId = userId

            response
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

        return try {
            val response = apolloClient.query(query).execute()

            response.data?.let { users ->
                Result.success(users.users.map { it.userShort.toDomain() })
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
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