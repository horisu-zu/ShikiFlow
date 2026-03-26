package com.example.shikiflow.data.local.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackDto
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity.Companion.toDto
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.UserRateType
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class AnimeTracksMediator(
    private val mediaTracksDataSource: MediaTracksDataSource,
    private val appRoomDatabase: AppRoomDatabase,
    private val userRateStatus: UserRateStatus,
    private val userId: Int?
): RemoteMediator<Int, AnimeTrackDto>() {

    private val animeTracksDao = appRoomDatabase.animeTracksDao()

    companion object {
        private val loadedPagesMap = mutableMapOf<UserRateStatus, Int>()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AnimeTrackDto>
    ): MediatorResult {
        return try {
            val page = when(loadType) {
                LoadType.REFRESH -> {
                    loadedPagesMap[userRateStatus] = 1
                    1
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val currentPage = loadedPagesMap[userRateStatus] ?: 1
                    if (state.lastItemOrNull() == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val nextPage = currentPage + 1
                    loadedPagesMap[userRateStatus] = nextPage

                    Log.d("AnimeTracksMediator", "Loading APPEND page: $nextPage")
                    nextPage
                }
            }

            val response = mediaTracksDataSource.getAnimeTracks(
                page = page,
                limit = state.config.pageSize,
                status = userRateStatus,
                userId = userId,
                order = Sort(
                    type = UserRateType.UPDATED_AT,
                    direction = SortDirection.DESCENDING
                )
            )

            return response.fold(
                onSuccess = { data ->
                    val tracks = data.map { userRate ->
                        userRate.track.toDto()
                    }
                    val animeItems = data.map { userRate ->
                        userRate.anime.toDto()
                    }

                    appRoomDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            animeTracksDao.clearTracksByStatus(userRateStatus.name)
                            animeTracksDao.clearAnimeItemsByStatus(userRateStatus.name)
                        }
                        animeTracksDao.insertTracks(tracks)
                        animeTracksDao.insertAnimeItems(animeItems)
                    }

                    val endOfPaginationReached = data.isEmpty() || data.size < state.config.pageSize
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                },
                onFailure = {
                    Log.e("AnimeTracksMediator", "Error Loading data: ${response.exceptionOrNull()}")
                    MediatorResult.Error(response.exceptionOrNull() ?: Exception("Unknown error"))
                }
            )
        } catch (e: IOException) {
            Log.e("AnimeTracksMediator", "Error loading data: ${e.message}")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("AnimeTracksMediator", "Error loading data: ${e.message}")
            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e("AnimeTracksMediator", "Error loading data: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}