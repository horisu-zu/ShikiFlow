package com.example.shikiflow.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.MangaTracksQuery
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.dao.MangaTracksDao
import com.example.shikiflow.data.local.mediator.MangaTracksMediator
import com.example.shikiflow.data.mapper.MangaTrackMapper.toDomain
import com.example.shikiflow.data.mapper.MangaTrackMapper.toDto
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.track.manga.MangaUserTrack
import com.example.shikiflow.domain.repository.MangaTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MangaTracksRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val appRoomDatabase: AppRoomDatabase,
    private val mangaTracksDao: MangaTracksDao
): MangaTracksRepository {

    private val requestMutex = Mutex()

    override fun getMangaTracks(status: UserRateStatusEnum): Flow<PagingData<MangaTrack>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MangaTracksMediator(
                mangaTracksRepository = this,
                appRoomDatabase = appRoomDatabase,
                mangaTracksDao = mangaTracksDao,
                userRateStatus = status
            ),
            pagingSourceFactory = { mangaTracksDao.getTracksByStatus(status.name) }
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
        status: UserRateStatusEnum?,
        order: UserRateOrderInputType?
    ): Result<List<MangaTracksQuery.UserRate>> {
        val query = MangaTracksQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId),
            status = Optional.presentIfNotNull(status),
            order = Optional.presentIfNotNull(order)
        )

        Log.d("MangaTracksRepository", "Query for status $status: $query")

        return try {
            val response = apolloClient.query(query).execute()
            response.data?.let {
                Result.success(it.userRates)
            } ?: Result.failure(Exception(response.exception))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMangaTrack(mangaTrack: MangaUserTrack) {
        appRoomDatabase.withTransaction {
            mangaTracksDao.deleteTrack(mangaTrack.toDto())
            mangaTracksDao.insert(mangaTrack.toDto())
        }
    }
}