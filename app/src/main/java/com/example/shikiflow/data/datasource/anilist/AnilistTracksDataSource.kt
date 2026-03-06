package com.example.shikiflow.data.datasource.anilist

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.anilist.MediaListIDsQuery
import com.example.graphql.anilist.MediaListTracksQuery
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackDto.Companion.toDomain
import com.example.shikiflow.data.local.mediator.AnimeTracksMediator
import com.example.shikiflow.data.local.mediator.MangaTracksMediator
import com.example.shikiflow.data.local.source.TracksPagingSource
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toAnilistType
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistOrder
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toAnilistRateStatus
import com.example.shikiflow.data.mapper.local.AnimeEntityMapper.toAnimeDomain
import com.example.shikiflow.data.mapper.local.AnimeEntityMapper.toDomain
import com.example.shikiflow.data.mapper.local.MangaEntityMapper.toMangaDomain
import com.example.shikiflow.domain.model.common.SortDirection
import com.example.shikiflow.domain.model.track.UserRateOrder
import com.example.shikiflow.domain.model.track.UserRateOrderType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.AnilistUtils.flatMap
import com.example.shikiflow.utils.AnilistUtils.toResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.Result

@OptIn(ExperimentalPagingApi::class)
class AnilistTracksDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val appRoomDatabase: AppRoomDatabase,
): MediaTracksDataSource {

    override fun getAnimeTracks(
        status: UserRateStatus,
        userId: String?
    ): Flow<PagingData<AnimeTrack>> {
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
                userRateStatus = status,
                userId = userId
            ),
            pagingSourceFactory = { appRoomDatabase.animeTracksDao().getTracksByStatus(status.name) }
        ).flow.map { pagingData ->
            pagingData.map { track ->
                track.toDomain()
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
                pageSize = 10,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 10
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

    override suspend fun getAnimeTracks(
        page: Int,
        limit: Int,
        userId: String?,
        status: UserRateStatus?,
        order: UserRateOrder?,
        idsList: List<Int>?
    ): Result<List<AnimeTrack>> {
        val query = MediaListTracksQuery(
            type = Optional.present(MediaType.ANIME.toAnilistType()),
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId?.toInt()),
            status = Optional.presentIfNotNull(status?.toAnilistRateStatus()),
            order = Optional.presentIfNotNull(order?.toAnilistOrder()?.let { listOf(it) }),
            idsIn = Optional.presentIfNotNull(idsList)
        )

        val response = apolloClient.query(query).execute()

        return response.toResult().map { data ->
            data.Page
                ?.mediaList
                ?.let { list ->
                    list.mapNotNull { mediaList ->
                        mediaList?.mediaListShort?.toAnimeDomain()
                    }
                } ?: emptyList()
        }
    }

    override suspend fun browseAnimeTracks(
        page: Int,
        limit: Int,
        userId: String?,
        name: String?,
        userStatus: UserRateStatus?,
    ): Result<List<AnimeTrack>> {
        val idsQuery = MediaListIDsQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            type = Optional.presentIfNotNull(MediaType.ANIME.toAnilistType()),
            search = Optional.presentIfNotNull(name),
        )

        val response = apolloClient.query(idsQuery).execute()

        return response.toResult().flatMap { data ->
            val idsList = data.Page?.media?.mapNotNull { it?.id }
            Log.d("AnilistTracksDataSource", "Ids: $idsList")
            Log.d("AnilistTracksDataSource", "User ID: $userId")

            getAnimeTracks(
                page = 1, //No need to add page when I search by the IDs
                limit = limit,
                userId = userId,
                idsList = idsList,
                status = userStatus,
                order = UserRateOrder(
                    type = UserRateOrderType.SCORE,
                    sort = SortDirection.DESCENDING
                )
            )
        }
    }

    override fun getMangaTracks(status: UserRateStatus, userId: String?): Flow<PagingData<MangaTrack>> {
        return Pager(
            config = PagingConfig(pageSize = 30),
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
        val query = MediaListTracksQuery(
            type = Optional.present(MediaType.MANGA.toAnilistType()),
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId?.toInt()),
            status = Optional.presentIfNotNull(status?.toAnilistRateStatus()),
            order = Optional.presentIfNotNull(listOf(order?.toAnilistOrder()))
        )

        val response = apolloClient.query(query).execute()

        return response.toResult().map { data ->
            data.Page?.mediaList?.let { list ->
                list.mapNotNull { mediaList ->
                    mediaList?.mediaListShort?.toMangaDomain()
                }
            } ?: emptyList()
        }
    }
}