package com.example.shikiflow.data.local.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.graphql.type.SortOrderEnum
import com.example.graphql.type.UserRateOrderFieldEnum
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.dao.MangaTracksDao
import com.example.shikiflow.data.local.entity.mangatrack.MangaShortEntity.Companion.toEntity
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackDto
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackEntity.Companion.toEntity
import com.example.shikiflow.domain.repository.MangaTracksRepository
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MangaTracksMediator(
    private val mangaTracksRepository: MangaTracksRepository,
    private val appRoomDatabase: AppRoomDatabase,
    private val mangaTracksDao: MangaTracksDao,
    private val userRateStatus: UserRateStatusEnum
): RemoteMediator<Int, MangaTrackDto>() {

    companion object {
        private val loadedPagesMap = mutableMapOf<UserRateStatusEnum, Int>()
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
            val response = mangaTracksRepository.getMangaTracks(
                page = page,
                limit = state.config.pageSize,
                status = userRateStatus,
                order = UserRateOrderInputType(
                    field = UserRateOrderFieldEnum.updated_at,
                    order = SortOrderEnum.desc
                )
            )

            return response.fold(
                onSuccess = { data ->
                    val tracks = data.map { it.mangaUserRateWithModel.toEntity() }
                    val mangaItems = data
                        .mapNotNull { it.mangaUserRateWithModel.manga?.mangaShort }
                        .map { it.toEntity() }

                    appRoomDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            mangaTracksDao.clearTracks(userRateStatus.name)
                            mangaTracksDao.clearMangaItems(userRateStatus.name)
                        }
                        appRoomDatabase.mangaTracksDao().insertTracks(tracks)
                        appRoomDatabase.mangaTracksDao().insertMangaItems(mangaItems)
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