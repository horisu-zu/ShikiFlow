package com.example.shikiflow.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.graphql.AnimeTracksQuery
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.dao.AnimeTracksDao
import com.example.shikiflow.data.local.mediator.AnimeTracksMediator
import com.example.shikiflow.data.mapper.AnimeTrackMapper.toDomain
import com.example.shikiflow.data.mapper.AnimeTrackMapper.toDto
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class AnimeTracksRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val appRoomDatabase: AppRoomDatabase,
    private val animeTracksDao: AnimeTracksDao
) : AnimeTracksRepository {

    override fun getAnimeTracks(status: UserRateStatusEnum): Flow<PagingData<AnimeTrack>> {
        return Pager(
            config = PagingConfig(pageSize = 21),
            remoteMediator = AnimeTracksMediator(
                animeTracksRepository = this,
                appRoomDatabase = appRoomDatabase,
                animeTracksDao = animeTracksDao,
                userRateStatus = status
            ),
            pagingSourceFactory = { animeTracksDao.getTracksByStatus(status.name) }
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
        status: UserRateStatusEnum?,
        order: UserRateOrderInputType?
    ): Result<List<AnimeTracksQuery.UserRate>> {
        val query = AnimeTracksQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            userId = Optional.presentIfNotNull(userId),
            status = Optional.presentIfNotNull(status),
            order = Optional.presentIfNotNull(order)
        )

        return try {
            val response = apolloClient.query(query).execute()
            response.data?.let {
                Result.success(it.userRates)
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAnimeTrack(animeTrack: AnimeUserTrack) {
        appRoomDatabase.withTransaction {
            animeTracksDao.deleteTrack(animeTrack.toDto())
            animeTracksDao.insert(animeTrack.toDto())
        }
    }
}