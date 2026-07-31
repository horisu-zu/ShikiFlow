package com.example.shikiflow.data.datasource.shikimori

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.example.graphql.shikimori.CurrentUserQuery
import com.example.graphql.shikimori.UsersQuery
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.data.mapper.shikimori.ShikimoriRateMapper.toDomain
import com.example.shikiflow.data.mapper.shikimori.ShikimoriUserMapper.mapUserStats
import com.example.shikiflow.data.mapper.shikimori.ShikimoriUserMapper.toDomain
import com.example.shikiflow.di.annotations.ShikimoriApollo
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.thread.Like
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.user.activity.UserActivity
import com.example.shikiflow.domain.model.user.UserFollow
import com.example.shikiflow.domain.model.user.UserSettings
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.model.user.activity.ActivityReply
import com.example.shikiflow.domain.model.user.social.Follower
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.UserSocial
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.io.IOException

class ShikimoriUserDataSource @Inject constructor(
    @param:ShikimoriApollo private val apolloClient: ApolloClient,
    private val userApi: UserApi
): UserDataSource, BaseNetworkRepository() {
    override fun fetchCurrentUser(): Flow<DataResult<User>> {
        return apolloClient.query(CurrentUserQuery())
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()
            .asDataResult { data ->
                data.currentUser?.userShort?.toDomain()
                    ?: throw IllegalStateException("No user data returned")
            }
    }

    override fun getUserActivity(
        userId: Int,
        page: Int,
        limit: Int
    ): Flow<PagedResult<UserActivity>> = flow<PagedResult<UserActivity>> {
        try {
            val result = userApi.getUserHistory(
                userId = userId.toLong(),
                page = page,
                limit = limit
            ).take(limit).map { response ->
                response.toDomain()
            }

            emit(
                PagedResult.Success(
                    list = result,
                    currentPage = page,
                    hasNextPage = result.size >= limit
                )
            )
        } catch (e: IOException) {
            emit(PagedResult.Error("${e.message}: Missing Internet Connection"))
        } catch (e: HttpException) {
            emit(PagedResult.Error(e.message ?: "Unknown Error"))
        }
    }.onStart { emit(PagedResult.Loading) }

    override fun getSingleActivity(activityId: Int): Flow<DataResult<UserActivity>> {
        TODO("No need to be implented")
    }

    override fun getActivityReplies(
        activityId: Int,
        page: Int,
        limit: Int
    ): Flow<PagedResult<ActivityReply>> {
        TODO("Can not be implemented")
    }

    override suspend fun submitActivityReply(
        id: Int?,
        activityId: Int,
        body: String
    ): DataResult<ActivityReply> {
        TODO("Can not be implemented")
    }

    override suspend fun deleteActivityReply(id: Int): DataResult<Boolean> {
        TODO("Can not be implemented")
    }

    override suspend fun toggleLike(id: Int, type: LikeableType): DataResult<Like> {
        TODO("Can not be implemented")
    }

    override suspend fun getUserStatsCategories(
        userId: Int,
        isRefresh: Boolean
    ): DataResult<UserStatsCategories> {
        return try {
            val (userRates, favorites, friends) = coroutineScope {
                val userRates = async {
                    userApi.getUserRates(userId = userId.toLong())
                        .map { response -> response.toDomain() }
                        .toDomain()
                }
                val favorites = async { getShikiFavorites(userId) }
                val friends = async {
                    userApi.getUserFriends(userId.toLong()).map { shikiUser ->
                        shikiUser.toDomain()
                    }
                }

                Triple(userRates.await(), favorites.await(), friends.await())
            }

            val userStatsCategories = mapUserStats(userRates, favorites, friends)

            DataResult.Success(userStatsCategories)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: "Unknown Error")
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

    override fun getUserGenres(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat<Genre>>>>> {
        return emptyFlow()
    }

    override fun getUserTags(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat<MediaTagEnum>>>>> {
        return emptyFlow()
    }

    override fun getUserStaff(userId: Int): Flow<DataResult<MediaTypeStats<List<StaffStat>>>> {
        return emptyFlow()
    }

    override fun getUserVoiceActors(userId: Int): Flow<DataResult<List<StaffStat>>> {
        return emptyFlow()
    }

    override fun getUserStudios(userId: Int): Flow<DataResult<List<StudioStat>>> {
        return emptyFlow()
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

    override fun getFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<DataResult<List<UserFavorite>>> = flow {
        try {
            val favorites = getShikiFavorites(userId)
                .filter { favorite ->
                    if (favoriteCategory == FavoriteCategory.STAFF) {
                        favorite.favoriteCategory in FavoriteCategory.shikiStaffEntries
                    } else {
                        favorite.favoriteCategory == favoriteCategory
                    }
                }

            emit(DataResult.Success(favorites))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }.onStart { emit(DataResult.Loading) }

    override fun getUserSocial(
        userId: Int,
        socialCategory: SocialCategory,
        page: Int,
        limit: Int
    ): Flow<PagedResult<UserSocial>> = flow {
        emit(PagedResult.Loading)

        val result = getUserFriends(userId, page, limit)

        result.fold(
            onSuccess = { data ->
                emit(PagedResult.Success(
                    list = data,
                    currentPage = page,
                    hasNextPage = data.size == limit
                ))
            },
            onFailure = { throwable ->
                emit(PagedResult.Error(throwable.message ?: "Unknown Error"))
            }
        )
    }

    override suspend fun getMediaRates(userId: Int, mediaType: MediaType): List<ShortUserMediaRate> {
        return when(mediaType) {
            MediaType.ANIME -> userApi.getShortUserAnimeRates(userId.toLong())
            MediaType.MANGA -> userApi.getShortUserMangaRates(userId.toLong())
        }.map { it.toDomain() }
    }

    private suspend fun getShikiFavorites(
        userId: Int
    ): List<UserFavorite> = userApi.getUserFavorites(userId.toLong()).toDomain()

    private suspend fun getUserFriends(
        userId: Int,
        page: Int,
        limit: Int
    ): Result<List<UserSocial>> {
        return try {
            val response = userApi.getUserFriends(
                userId = userId.toLong(),
                page = page,
                limit = limit
            ).map { shikiUser ->
                Follower(
                    data = shikiUser.toDomain()
                )
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsersByNickname(
        page: Int,
        limit: Int,
        nickname: String
    ): Result<List<Browse.User>> {
        if(nickname.isBlank()) return Result.success(emptyList())

        val query = UsersQuery(
            page = Optional.present(page),
            limit = Optional.present(limit),
            search = Optional.present(nickname)
        )

        val response = apolloClient.query(query).execute()

        return response.toResult().map { data ->
            data.users.map { userData ->
                Browse.User(
                    data = userData.userShort.toDomain()
                )
            }
        }
    }

    override suspend fun getFollow(userId: Int): DataResult<UserFollow> {
        return try {
            val response = userApi.getUserFollow(userId.toLong())

            DataResult.Success(UserFollow(isFollowing = response.isFollowing))
        } catch (e: Exception) {
            DataResult.Error(e.message ?: "Unknown Error")
        }
    }

    override suspend fun toggleFavorite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?,
        isFavorite: Boolean
    ): DataResult<Unit> {
        return try {
            val pair = when {
                animeId != null -> "Anime" to animeId
                mangaId != null -> "Manga" to mangaId
                characterId != null -> "Character" to characterId
                staffId != null -> "Person" to staffId
                else -> "" to 0
            }

            val response = when(isFavorite) {
                false -> {
                    if (staffId != null) {
                        userApi.createFavoriteStaff(linkedType = pair.first, id = pair.second)
                    } else {
                        userApi.createFavorite(linkedType = pair.first, id = pair.second)
                    }
                }
                true -> {
                    userApi.deleteFavorite(linkedType = pair.first, id = pair.second)
                }
            }

            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: "Unknown Error")
        }
    }

    override suspend fun toggleFollow(
        userId: Int,
        isFollowing: Boolean
    ): DataResult<Boolean> {
        return try {
            when(isFollowing) {
                true -> {
                    userApi.addFriend(userId.toLong())

                    DataResult.Success(true)
                }
                false -> {
                    userApi.deleteFriend(userId.toLong())

                    DataResult.Success(false)
                }
            }
        } catch (e: Exception) {
            DataResult.Error(e.message ?: "Unknown Error")
        }
    }

    override suspend fun setUserSettings(
        preferredTitleType: PreferredTitleType?,
        scoreFormat: ScoreFormat?
    ): DataResult<UserSettings> {
        TODO("AniList API only")
    }
}