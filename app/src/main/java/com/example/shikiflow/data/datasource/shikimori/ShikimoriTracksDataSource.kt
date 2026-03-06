package com.example.shikiflow.data.datasource.shikimori

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.shikimori.AnimeTrackBrowseQuery
import com.example.graphql.shikimori.AnimeTracksQuery
import com.example.graphql.shikimori.MangaTracksQuery
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackDto.Companion.toDomain
import com.example.shikiflow.data.local.mediator.AnimeTracksMediator
import com.example.shikiflow.data.local.mediator.MangaTracksMediator
import com.example.shikiflow.data.local.source.TracksPagingSource
import com.example.shikiflow.data.mapper.common.OrderMapper.toShikimoriOrder
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toShikimoriRateStatus
import com.example.shikiflow.data.mapper.local.AnimeEntityMapper.toDomain
import com.example.shikiflow.data.mapper.local.MangaEntityMapper.toDomain
import com.example.shikiflow.domain.model.track.UserRateOrder
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.utils.AnilistUtils.toResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

@OptIn(ExperimentalPagingApi::class)
class ShikimoriTracksDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val appRoomDatabase: AppRoomDatabase
): MediaTracksDataSource {

    override fun getAnimeTracks(status: UserRateStatus, userId: String?): Flow<PagingData<AnimeTrack>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                prefetchDistance = 15,
                initialLoadSize = 30
            ),
            remoteMediator = AnimeTracksMediator(
                mediaTracksDataSource = this,
                appRoomDatabase = appRoomDatabase,
                userRateStatus = status
            ),
            pagingSourceFactory = { appRoomDatabase.animeTracksDao().getTracksByStatus(status.name) }
        ).flow.map { pagingData ->
            pagingData.map { track ->
                track.toDomain()
            }
        }
    }

    override suspend fun getAnimeTracks(
        page: Int,
        limit: Int,
        userId: String?,
        status: UserRateStatus?,
        order: UserRateOrder?,
        idsList: List<Int>?
    ): Result<List<AnimeTrack>> {
        val query = AnimeTracksQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId),
            status = Optional.presentIfNotNull(status?.toShikimoriRateStatus()),
            order = Optional.presentIfNotNull(order?.toShikimoriOrder())
        )

        val response = apolloClient.query(query).execute()
        return response.toResult().map { data ->
            data.userRates.map { userRate ->
                userRate.animeUserRateWithModel.toDomain()
            }
        }
    }

    override fun getBrowseTracks(
        userId: String?,
        title: String,
        userRateStatus: UserRateStatus?
    ): Flow<PagingData<AnimeTrack>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                TracksPagingSource(
                    mediaTracksDataSource = this,
                    userId = userId,
                    userStatus = userRateStatus,
                    title = title
                )
            }
        ).flow
    }

    override suspend fun browseAnimeTracks(
        page: Int,
        limit: Int,
        userId: String?,
        name: String?,
        userStatus: UserRateStatus?
    ): Result<List<AnimeTrack>> {
        val query = AnimeTrackBrowseQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            search = Optional.presentIfNotNull(name),
            mylist = when(userStatus) {
                null -> Optional.present(
                    value = UserRateStatus.entries
                        .filter { it != UserRateStatus.UNKNOWN }
                        .joinToString(",") { it.toShikimoriRateStatus().name }
                )
                else -> Optional.present(userStatus.toShikimoriRateStatus().name)
            }
        )
        Log.d("ShikimoriTracksDataSource", "Query: $query")

        val response = apolloClient.query(query).execute()

        return response.toResult().map { data ->
            data.animes.mapNotNull { anime ->
                anime.userRate?.animeUserRateWithModel?.toDomain()
            }
        }
    }

    override fun getMangaTracks(status: UserRateStatus, userId: String?): Flow<PagingData<MangaTrack>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MangaTracksMediator(
                mediaTracksDataSource = this,
                appRoomDatabase = appRoomDatabase,
                userRateStatus = status,
                userId = userId
            ),
            pagingSourceFactory = { appRoomDatabase.mangaTracksDao().getTracksByStatus(status.name) }
        ).flow.map { pagingData ->
            pagingData.map { track ->
                track.toDomain()
            }
        }
    }

    override suspend fun getMangaTracks(
        page: Int,
        limit: Int,
        userId: String?,
        status: UserRateStatus?,
        order: UserRateOrder?
    ): Result<List<MangaTrack>> {
        val query = MangaTracksQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId),
            status = Optional.presentIfNotNull(status?.toShikimoriRateStatus()),
            order = Optional.presentIfNotNull(order?.toShikimoriOrder())
        )

        Log.d("MangaTracksRepository", "Query for status $status: $query")

        val response = apolloClient.query(query).execute()

        return response.toResult().map { data ->
            data.userRates.map { userRate ->
                userRate.mangaUserRateWithModel.toDomain()
            }
        }
    }
}