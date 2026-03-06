package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.anilist.CurrentUserQuery
import com.example.graphql.anilist.SaveUserRateMutation
import com.example.graphql.anilist.ShortUserRateQuery
import com.example.graphql.anilist.UserActivitiesQuery
import com.example.graphql.anilist.UserFavoriteCategoriesQuery
import com.example.graphql.anilist.UserRatesQuery
import com.example.graphql.anilist.UsersQuery
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.data.local.source.FavoritesPagingSource
import com.example.shikiflow.data.local.source.HistoryPagingSource
import com.example.shikiflow.data.local.source.UserPagingSource
import com.example.shikiflow.data.mapper.anilist.AnilistRateMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toAnilistType
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toAnilistRateStatus
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.UserHistory
import com.example.shikiflow.domain.model.user.UserRateStats
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.utils.AnilistUtils.toResult
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlin.let

class AnilistUserDataSource @Inject constructor(
    private val apolloClient: ApolloClient
): UserDataSource {

    override suspend fun fetchCurrentUser(): User? {
        val query = apolloClient.query(CurrentUserQuery()).execute()

        return query.data?.Viewer?.aLUserShort?.toDomain()
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
        val historyQuery = UserActivitiesQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.present(userId)
        )

        val response = apolloClient.query(historyQuery).execute()

        return response.data?.Page?.activities?.let { activities ->
            activities.mapNotNull { it?.onListActivity?.aLUserActivity?.toDomain() }
        } ?: emptyList()
    }

    override suspend fun getUserRates(userId: Int): UserRateStats {
        val ratesQuery = UserRatesQuery(userId)

        val response = apolloClient.query(ratesQuery).execute()

        val userStats = response.data?.User?.statistics ?:
            throw IllegalStateException("No user statistics data returned")

        val mediaStats = userStats.let { userStats ->
            val animeStats = userStats.anime?.aLUserListStats?.toDomain()
            val mangaStats = userStats.manga?.aLUserListStats?.toDomain()

            buildMap {
                if(!animeStats?.scoreStats.isNullOrEmpty()) {
                    put(MediaType.ANIME, animeStats)
                }
                if(!mangaStats?.scoreStats.isNullOrEmpty()) {
                    put(MediaType.MANGA, mangaStats)
                }
            }
        }

        return UserRateStats(mediaStats)
    }

    override suspend fun getFavoriteCategories(userId: Int): List<FavoriteCategory> {
        val categoriesQuery = UserFavoriteCategoriesQuery(userId)

        val response = apolloClient.query(categoriesQuery).execute()

        val favorites = response.data?.User?.favourites
            ?: throw IllegalStateException("No data returned from UserFavoriteCategories")

        val favoritesMap = mapOf(
            FavoriteCategory.ANIME to (favorites.anime?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.MANGA to (favorites.manga?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.CHARACTER to (favorites.characters?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.STAFF to (favorites.staff?.pageInfo?.aLPageInfoShort?.total ?: 0),
            FavoriteCategory.STUDIO to (favorites.studios?.pageInfo?.aLPageInfoShort?.total ?: 0)
        )

        return favoritesMap.filter { it.value > 0 }.keys.toList()
    }

    override fun getUserFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<PagingData<UserFavorite>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                FavoritesPagingSource(
                    apolloClient = apolloClient,
                    userId = userId,
                    favoriteCategory = favoriteCategory
                )
            }
        ).flow
    }

    override suspend fun getMediaRates(userId: Int, mediaType: MediaType): List<ShortUserMediaRate> {
        val ratesQuery = ShortUserRateQuery(userId, mediaType.toAnilistType())

        val response = apolloClient.query(ratesQuery).execute()

        val result = response.data
            ?.MediaListCollection
            ?.lists
            ?.flatMap { listEntry ->
                listEntry?.entries?.mapNotNull { entry ->
                    entry?.aLRateEntryShort?.toDomain()
                } ?: emptyList()
            } ?: throw IllegalStateException("No data returned from Short User Rate Query")

        return result
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
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            search = Optional.presentIfNotNull(nickname)
        )

        val response = apolloClient.query(query).execute()

        return response.toResult().map { data ->
            data.Page?.users
                ?.mapNotNull { user ->
                    user?.aLUserShort?.toDomain()
                } ?: emptyList()
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
        val userRateQuery = SaveUserRateMutation(
            rateEntryId = Optional.presentIfNotNull(entryId),
            mediaId = mediaId,
            mediaStatus = status.toAnilistRateStatus(),
            progress = Optional.presentIfNotNull(progress),
            progressVolumes = Optional.presentIfNotNull(progressVolumes),
            repeat = Optional.presentIfNotNull(repeat),
            scoreRaw = Optional.presentIfNotNull(score?.times(10))
        )

        val response = apolloClient.mutation(userRateQuery).execute()

        val userRate = response.data
            ?.SaveMediaListEntry
            ?.aLRateEntry
            ?: throw IllegalStateException("No data returned from SaveMediaListEntry")

        return userRate.toDomain()
    }
}