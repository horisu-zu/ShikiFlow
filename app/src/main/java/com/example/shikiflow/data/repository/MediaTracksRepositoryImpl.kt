package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.mangatrack.MangaShortEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackEntity.Companion.toDto
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.anime.AnimeShortData
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import com.example.shikiflow.domain.model.track.manga.MangaShortData
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.track.manga.MangaUserTrack
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class MediaTracksRepositoryImpl @Inject constructor(
    private val shikimoriTracksDataSource: MediaTracksDataSource,
    private val anilistTracksDataSource: MediaTracksDataSource,
    private val settingsRepository: SettingsRepository,
    private val appRoomDatabase: AppRoomDatabase
): MediaTracksRepository {

    val animeTracksDao = appRoomDatabase.animeTracksDao()
    val mangaTracksDao = appRoomDatabase.mangaTracksDao()

    private fun getSource() = runBlocking {
        when(settingsRepository.authTypeFlow.first()) {
            AuthType.SHIKIMORI -> shikimoriTracksDataSource
            AuthType.ANILIST -> anilistTracksDataSource
        }
    }

    override fun getAnimeTracks(status: UserRateStatus, userId: Int?): Flow<PagingData<AnimeTrack>> {
        return getSource().getAnimeTracks(status, userId)
    }

    override fun getBrowseTracks(
        userId: Int?,
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
                GenericPagingSource(
                    method = { page, limit ->
                        getSource().browseAnimeTracks(page, limit, userId, title, userRateStatus)
                    }
                )
            }
        ).flow
    }

    override suspend fun updateAnimeTrack(
        animeTrack: AnimeUserTrack,
        animeShortData: AnimeShortData?
    ) {
        val track = animeTrack.toDto()

        appRoomDatabase.withTransaction {
            when(animeShortData) {
                null -> animeTracksDao.updateTrack(track)
                else -> {
                    animeTracksDao.insertShortEntity(animeShortData.toDto())
                    animeTracksDao.insertTrack(track)
                }
            }
        }
    }

    override fun getMangaTracks(
        status: UserRateStatus,
        userId: Int?
    ): Flow<PagingData<MangaTrack>> = getSource().getMangaTracks(status, userId = userId)

    override suspend fun updateMangaTrack(
        mangaTrack: MangaUserTrack,
        mangaShortData: MangaShortData?
    ) {
        val track = mangaTrack.toDto()

        appRoomDatabase.withTransaction {
            when(mangaShortData) {
                null -> mangaTracksDao.updateTrack(track)
                else -> {
                    mangaTracksDao.insertShortEntity(mangaShortData.toDto())
                    mangaTracksDao.insertTrack(track)
                }
            }
        }
    }
}