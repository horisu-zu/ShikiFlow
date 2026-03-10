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
import com.example.shikiflow.data.local.entity.mangatrack.MangaShortEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackDto
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackEntity.Companion.toDto
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.UserRateOrder
import com.example.shikiflow.domain.model.sort.UserRateOrderType
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MangaTracksMediator(
    private val mediaTracksDataSource: MediaTracksDataSource,
    private val appRoomDatabase: AppRoomDatabase,
    private val userRateStatus: UserRateStatus,
    private val userId: String?
): RemoteMediator<Int, MangaTrackDto>() {

    private val mangaTracksDao = appRoomDatabase.mangaTracksDao()

    companion object {
        private val loadedPagesMap = mutableMapOf<UserRateStatus, Int>()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MangaTrackDto>
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

                    Log.d("MangaTracksMediator", "Loading APPEND page: $nextPage")
                    nextPage
                }
            }

            Log.d("MangaTracksMediator", "Attempting to load tracks for status: $userRateStatus")
            val response = mediaTracksDataSource.getMangaTracks(
                page = page,
                limit = state.config.pageSize,
                userId = userId,
                status = userRateStatus,
                order = UserRateOrder(
                    type = UserRateOrderType.UPDATED_AT,
                    sort = SortDirection.DESCENDING
                )
            )

            return response.fold(
                onSuccess = { data ->
                    val tracks = data.map { it.track.toDto() }
                    val mangaItems = data.map { it.manga.toDto() }

                    appRoomDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            mangaTracksDao.clearTracks(userRateStatus.name)
                            mangaTracksDao.clearMangaItems(userRateStatus.name)
                        }
                        mangaTracksDao.insertTracks(tracks)
                        mangaTracksDao.insertMangaItems(mangaItems)
                    }

                    val endOfPaginationReached = data.isEmpty() || data.size < state.config.pageSize
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                },
                onFailure = { exception ->
                    MediatorResult.Error(exception)
                }
            )
        } catch (e: IOException) {
            Log.e("MangaTracksMediator", "Error loading data: ${e.message}")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("MangaTracksMediator", "Error loading data: ${e.message}")
            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e("MangaTracksMediator", "Error loading data: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}