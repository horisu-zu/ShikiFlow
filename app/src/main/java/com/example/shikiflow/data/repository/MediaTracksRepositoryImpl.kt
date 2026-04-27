package com.example.shikiflow.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.mediator.MediaTracksMediator
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.data.mapper.local.MediaShortMapper.toEntity
import com.example.shikiflow.data.mapper.local.MediaTrackMapper.toEntity
import com.example.shikiflow.data.mapper.local.MediaTrackMapper.toMediaEntity
import com.example.shikiflow.data.mapper.local.TracksMapper.toDomain
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.Shikimori
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.media.MediaShortData
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.domain.model.track.media.MediaUserTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MediaTracksRepositoryImpl @Inject constructor(
    @param:Shikimori private val shikimoriTracksDataSource: MediaTracksDataSource,
    @param:AniList private val anilistTracksDataSource: MediaTracksDataSource,
    private val settingsRepository: SettingsRepository,
    private val appRoomDatabase: AppRoomDatabase,
    private val scope: CoroutineScope
): MediaTracksRepository, BaseNetworkRepository() {
    val mediaTracksDao = appRoomDatabase.mediaTracksDao()

    private val dataSource = settingsRepository.authTypeFlow
        .filterNotNull()
        .map { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> shikimoriTracksDataSource
                AuthType.ANILIST -> anilistTracksDataSource
            }
        }
        .distinctUntilChanged()

    override fun getLocalTrack(malId: Int, mediaType: MediaType): Flow<MediaTrack?> {
        return mediaTracksDao.getTrackByMalId(malId, mediaType)
            .map { dto ->
                dto?.toDomain()
            }
    }

    override fun getMediaTracks(
        status: UserRateStatus,
        userId: Int?,
        mediaType: MediaType
    ): Flow<PagingData<MediaTrack>> {
        return withSource(dataSource) { tracksDataSource ->
            Pager(
                config = PagingConfig(
                    pageSize = 24,
                    enablePlaceholders = true,
                    prefetchDistance = 12,
                    initialLoadSize = 24
                ),
                remoteMediator = MediaTracksMediator(
                    mediaTracksDataSource = tracksDataSource,
                    appRoomDatabase = appRoomDatabase,
                    userRateStatus = status,
                    userId = userId,
                    mediaType = mediaType
                ),
                pagingSourceFactory = { mediaTracksDao.getTracksByStatus(status.name, mediaType) }
            ).flow.map { pagingData ->
                pagingData.map { track ->
                    track.toDomain()
                }
            }
        }
    }

    override fun browseMediaTracks(
        userId: Int,
        mediaType: MediaType,
        title: String,
        userRateStatus: UserRateStatus?
    ): Flow<PagingData<MediaTrack>> {
        return withSource(dataSource) { dataSource ->
            Pager(
                config = PagingConfig(
                    pageSize = 24,
                    enablePlaceholders = true,
                    prefetchDistance = 12,
                    initialLoadSize = 24
                ),
                pagingSourceFactory = {
                    GenericPagingSource(
                        method = { page, limit ->
                            dataSource.browseMediaTracks(page, limit, mediaType, userId, title, userRateStatus)
                        }
                    )
                }
            ).flow
        }
    }

    override fun saveUserRate(
        entryId: Int?,
        mediaType: MediaType,
        mediaId: Int,
        malId: Int?,
        status: UserRateStatus,
        progress: Int?,
        progressVolumes: Int?,
        repeat: Int?,
        score: Int?,
        mediaShortData: MediaShortData?
    ): Flow<DataResult<UserMediaRate>> = flow {
        emit(DataResult.Loading)

        try {
            withSourceSuspend(
                flow = settingsRepository.settingsFlow
                    .map { settings -> settings.serviceUpdateState }
            ) { serviceUpdateState ->
                if(serviceUpdateState) {
                    scope.launch {
                        saveServiceUserRate(
                            mediaType,
                            malId,
                            status,
                            progress,
                            progressVolumes,
                            repeat,
                            score
                        )
                    }
                }
            }

            val result = withSourceSuspend(dataSource) { dataSource ->
                settingsRepository.userFlow.first()
                    .let { user ->
                        dataSource.saveUserRate(
                            userId = user?.id,
                            entryId = entryId,
                            mediaType = mediaType,
                            mediaId = mediaId,
                            status = status,
                            progress = progress,
                            progressVolumes = progressVolumes,
                            repeat = repeat,
                            score = score
                        )
                    }
            }

            updateMediaTrack(
                mediaTrack = result.toMediaEntity(),
                mediaShortData = if(entryId != null) null else mediaShortData
            )

            emit(DataResult.Success(result))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }

    suspend fun saveServiceUserRate(
        mediaType: MediaType,
        malId: Int?,
        status: UserRateStatus,
        progress: Int?,
        progressVolumes: Int?,
        repeat: Int?,
        score: Int?
    ) {
        try {
            withSourceSuspend(
                flow = settingsRepository.connectedServicesFlow
            ) { connectedServices ->
                connectedServices.forEach { (type, user) ->
                    malId?.let {
                        when(type) {
                            AuthType.SHIKIMORI -> shikimoriTracksDataSource
                            AuthType.ANILIST -> anilistTracksDataSource
                        }.saveServiceUserRate(
                            userId = user.id,
                            mediaType = mediaType,
                            malId = malId,
                            status = status,
                            progress = progress,
                            progressVolumes = progressVolumes,
                            repeat = repeat,
                            score = score
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MediaTracksRepository", "Error: ${e.message}")
        }
    }

    override suspend fun updateMediaTrack(
        mediaTrack: MediaUserTrack,
        mediaShortData: MediaShortData?
    ) {
        val track = mediaTrack.toEntity()

        appRoomDatabase.withTransaction {
            when(mediaShortData) {
                null -> mediaTracksDao.updateTrack(track)
                else -> {
                    mediaTracksDao.insertShortEntity(mediaShortData.toEntity())
                    mediaTracksDao.insertTrack(track)
                }
            }
        }
    }

    override fun deleteUserRate(
        entryId: Int,
        mediaId: Int,
        malId: Int?,
        mediaType: MediaType
    ): Flow<DataResult<Boolean>> = flow {
        emit(DataResult.Loading)

        try {
            withSourceSuspend(
                flow = settingsRepository.settingsFlow
                    .map { settings -> settings.serviceUpdateState }
            ) { serviceUpdateState ->
                if(serviceUpdateState) {
                    scope.launch {
                        deleteServiceUserRate(malId, mediaType)
                    }
                }
            }

            withSourceSuspend(dataSource) { dataSource ->
                val result = dataSource.deleteUserRate(entryId)

                emit(result)
            }

            appRoomDatabase.withTransaction {
                mediaTracksDao.deleteTrack(entryId)
                mediaTracksDao.deleteMedia(mediaId, mediaType)
            }
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }

    suspend fun deleteServiceUserRate(
        malId: Int?,
        mediaType: MediaType
    ) {
        try {
            withSourceSuspend(
                flow = settingsRepository.connectedServicesFlow
            ) { connectedServices ->
                connectedServices.forEach { (type, user) ->
                    malId?.let {
                        when(type) {
                            AuthType.SHIKIMORI -> shikimoriTracksDataSource
                            AuthType.ANILIST -> anilistTracksDataSource
                        }.deleteServiceUserRate(user.id, malId, mediaType)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MediaTracksRepository", "Error: ${e.message}")
        }
    }
}