package com.example.shikiflow.data.repository

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
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.media.MediaShortData
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.domain.model.track.media.MediaUserTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
class MediaTracksRepositoryImpl @Inject constructor(
    private val shikimoriTracksDataSource: MediaTracksDataSource,
    private val anilistTracksDataSource: MediaTracksDataSource,
    private val settingsRepository: SettingsRepository,
    private val appRoomDatabase: AppRoomDatabase
): MediaTracksRepository {
    val mediaTracksDao = appRoomDatabase.mediaTracksDao()

    private fun getSource() = runBlocking {
        when(settingsRepository.authTypeFlow.first()) {
            AuthType.SHIKIMORI -> shikimoriTracksDataSource
            AuthType.ANILIST -> anilistTracksDataSource
        }
    }

    override fun getMediaTracks(
        status: UserRateStatus,
        userId: Int?,
        mediaType: MediaType
    ): Flow<PagingData<MediaTrack>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            remoteMediator = MediaTracksMediator(
                mediaTracksDataSource = getSource(),
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

    override fun browseMediaTracks(
        userId: Int,
        mediaType: MediaType,
        title: String,
        userRateStatus: UserRateStatus?
    ): Flow<PagingData<MediaTrack>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    method = { page, limit ->
                        getSource().browseMediaTracks(page, limit, mediaType, userId, title, userRateStatus)
                    }
                )
            }
        ).flow
    }

    override fun saveUserRate(
        userId: Int?,
        entryId: Int?,
        mediaType: MediaType,
        mediaId: Int,
        status: UserRateStatus,
        progress: Int?,
        progressVolumes: Int?,
        repeat: Int?,
        score: Int?,
        mediaShortData: MediaShortData?
    ): Flow<DataResult<UserMediaRate>> = flow {
        emit(DataResult.Loading)

        try {
            val result = getSource().saveUserRate(
                userId = userId,
                entryId = entryId,
                mediaType = mediaType,
                mediaId = mediaId,
                status = status,
                progress = progress,
                progressVolumes = progressVolumes,
                repeat = repeat,
                score = score
            )

            updateMediaTrack(
                mediaTrack = result.toMediaEntity(),
                mediaShortData = if(entryId != null) null else mediaShortData
            )

            emit(DataResult.Success(result))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
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
}