package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.example.graphql.anilist.CurrentUserQuery
import com.example.graphql.anilist.SaveUserRateMutation
import com.example.graphql.anilist.ShortUserRateQuery
import com.example.graphql.anilist.UserActivitiesQuery
import com.example.graphql.anilist.UserFollowersQuery
import com.example.graphql.anilist.UserFollowingsQuery
import com.example.graphql.anilist.UserGenresQuery
import com.example.graphql.anilist.UserSearchQuery
import com.example.graphql.anilist.UserStaffQuery
import com.example.graphql.anilist.UserStatsCategoriesQuery
import com.example.graphql.anilist.UserStatsQuery
import com.example.graphql.anilist.UserStudiosQuery
import com.example.graphql.anilist.UserTagsQuery
import com.example.graphql.anilist.UserThreadCommentsQuery
import com.example.graphql.anilist.UserThreadsQuery
import com.example.graphql.anilist.UserVoiceActorsQuery
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.data.local.source.FavoritesPagingSource
import com.example.shikiflow.data.local.source.HistoryPagingSource
import com.example.shikiflow.data.local.source.SocialPagingSource
import com.example.shikiflow.data.local.source.UserPagingSource
import com.example.shikiflow.data.mapper.anilist.AnilistRateMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toGenreStats
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toOverviewStats
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toStaffStats
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toStudiosStats
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toTagsStats
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toAnilistType
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toAnilistRateStatus
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.user.UserActivity
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.model.user.social.Follower
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.Thread
import com.example.shikiflow.domain.model.user.social.UserSocial
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlin.let

class AnilistUserDataSource(
    private val apolloClient: ApolloClient
): UserDataSource, BaseNetworkRepository() {

    override fun fetchCurrentUser(): Flow<DataResult<User>> {
        return apolloClient.query(CurrentUserQuery())
            .toFlow()
            .asDataResult { data ->
                data.Viewer?.aLUserShort?.toDomain()
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
        val historyQuery = UserActivitiesQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.present(userId)
        )

        val response = apolloClient.query(historyQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.data?.Page?.activities?.let { activities ->
            activities.mapNotNull { activity ->
                activity?.onListActivity?.aLListActivity?.toDomain() ?:
                activity?.onMessageActivity?.aLMessageActivity?.toDomain() ?:
                activity?.onTextActivity?.aLTextActivity?.toDomain()
            }
        } ?: emptyList()
    }

    override fun getUserStatsCategories(
        userId: Int
    ): Flow<DataResult<UserStatsCategories>> {
        val userStatsCategoriesQuery = UserStatsCategoriesQuery(userId)

        val response = apolloClient.query(userStatsCategoriesQuery)
            .toFlow()
            .asDataResult { response ->
                response.toDomain()
            }

        return response
    }

    override fun getUserRates(
        userId: Int
    ): Flow<DataResult<MediaTypeStats<OverviewStats>>> {
        val ratesQuery = UserStatsQuery(userId)

        val response = apolloClient.query(ratesQuery)
            .toFlow()
            .asDataResult { data ->
                val userStats = data.User?.statistics ?:
                    throw IllegalStateException("No user statistics data returned")

                MediaTypeStats<OverviewStats>(
                    animeStats = userStats.anime?.aLUserListStats?.toOverviewStats(MediaType.ANIME),
                    mangaStats = userStats.manga?.aLUserListStats?.toOverviewStats(MediaType.MANGA)
                )
            }

        return response
    }

    override fun getUserGenres(
        userId: Int
    ): Flow<DataResult<MediaTypeStats<List<TypeStat>>>> {
        val genresQuery = UserGenresQuery(userId)

        val response = apolloClient.query(genresQuery)
            .toFlow()
            .asDataResult { data ->
                val userGenres = data.User?.statistics ?:
                    throw IllegalStateException("No user statistics data returned")

                MediaTypeStats<List<TypeStat>>(
                    animeStats = userGenres.anime?.aLUserGenres?.toGenreStats(),
                    mangaStats = userGenres.manga?.aLUserGenres?.toGenreStats()
                )
            }

        return response
    }

    override fun getUserTags(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat>>>> {
        val tagsQuery = UserTagsQuery(userId)

        val response = apolloClient.query(tagsQuery)
            .toFlow()
            .asDataResult { data ->
                val userGenres = data.User?.statistics ?:
                    throw IllegalStateException("No user statistics data returned")

                MediaTypeStats<List<TypeStat>>(
                    animeStats = userGenres.anime?.aLUserTags?.toTagsStats(),
                    mangaStats = userGenres.manga?.aLUserTags?.toTagsStats()
                )
            }

        return response
    }

    override fun getUserStaff(userId: Int): Flow<DataResult<MediaTypeStats<List<StaffStat>>>> {
        val staffQuery = UserStaffQuery(userId)

        val response = apolloClient.query(staffQuery)
            .toFlow()
            .asDataResult { data ->
                val userGenres = data.User?.statistics ?:
                    throw IllegalStateException("No user statistics data returned")

                MediaTypeStats<List<StaffStat>>(
                    animeStats = userGenres.anime?.aLUserStaff?.toStaffStats(),
                    mangaStats = userGenres.manga?.aLUserStaff?.toStaffStats()
                )
            }

        return response
    }

    override fun getUserVoiceActors(userId: Int): Flow<DataResult<List<StaffStat>>> {
        val voiceActorsQuery = UserVoiceActorsQuery(userId)

        val response = apolloClient.query(voiceActorsQuery)
            .toFlow()
            .asDataResult { data ->
                val userGenres = data.User?.statistics ?:
                    throw IllegalStateException("No user statistics data returned")

                userGenres.anime?.aLUserVoiceActors?.toStaffStats() ?: emptyList()
            }

        return response
    }

    override fun getUserStudios(userId: Int): Flow<DataResult<List<StudioStat>>> {
        val studiosQuery = UserStudiosQuery(userId)

        val response = apolloClient.query(studiosQuery)
            .toFlow()
            .asDataResult { data ->
                val userGenres = data.User?.statistics ?:
                    throw IllegalStateException("No user statistics data returned")

                userGenres.anime?.aLUserStudios?.toStudiosStats() ?: emptyList()
            }

        return response
    }

    override fun getUserFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<PagingData<UserFavorite>> {
        return Pager(
            config = PagingConfig(
                pageSize = 21,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 21
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

    override fun getUserSocial(
        userId: Int,
        socialCategory: SocialCategory
    ): Flow<PagingData<UserSocial>> {
        return Pager(
            config = PagingConfig(
                pageSize = 18,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 18
            ),
            pagingSourceFactory = {
                SocialPagingSource(
                    dataSource = this,
                    userId = userId,
                    socialCategory = socialCategory
                )
            }
        ).flow
    }

    suspend fun getUserFollowings(
        userId: Int,
        page: Int,
        limit: Int
    ): Result<List<UserSocial>> {
        val followingsQuery = UserFollowingsQuery(page, limit, userId)

        val response = apolloClient.query(followingsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.toResult().map { data ->
            data.Page
                ?.following
                ?.let { list ->
                    list.mapNotNull { user ->
                        user?.aLUserShort?.let { alUser ->
                            Follower(alUser.toDomain())
                        }
                    }
                } ?: emptyList()
        }
    }

    suspend fun getUserFollowers(
        userId: Int,
        page: Int,
        limit: Int
    ): Result<List<UserSocial>> {
        val followingsQuery = UserFollowersQuery(page, limit, userId)

        val response = apolloClient.query(followingsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.toResult().map { data ->
            data.Page
                ?.followers
                ?.let { list ->
                    list.mapNotNull { user ->
                        user?.aLUserShort?.let { alUser ->
                            Follower(alUser.toDomain())
                        }
                    }
                } ?: emptyList()
        }
    }

    suspend fun getUserThreads(
        userId: Int,
        page: Int,
        limit: Int
    ): Result<List<UserSocial>> {
        val followingsQuery = UserThreadsQuery(page, limit, userId)

        val response = apolloClient.query(followingsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.toResult().map { data ->
            data.Page
                ?.threads
                ?.let { list ->
                    list.mapNotNull { user ->
                        user?.aLThread?.let { alThread ->
                            Thread(alThread.toDomain())
                        }
                    }
                } ?: emptyList()
        }
    }

    suspend fun getUserThreadComments(
        userId: Int,
        page: Int,
        limit: Int
    ): Result<List<UserSocial>> {
        val followingsQuery = UserThreadCommentsQuery(page, limit, userId)

        val response = apolloClient.query(followingsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.toResult().map { data ->
            data.Page
                ?.threadComments
                ?.let { list ->
                    list.mapNotNull { user ->
                        user?.aLThreadCommentWithHeader?.toDomain()
                    }
                } ?: emptyList()
        }
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

    override fun getUsers(query: String): Flow<PagingData<Browse.User>> {
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
    ): Result<List<Browse.User>> {
        val query = UserSearchQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            search = Optional.presentIfNotNull(nickname)
        )

        val response = apolloClient.query(query).execute()

        return response.toResult().map { data ->
            data.Page?.users
                ?.mapNotNull { user ->
                    user?.aLUserShort?.let { alUser ->
                        Browse.User(
                            data = alUser.toDomain()
                        )
                    }
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