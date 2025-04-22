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
import com.example.shikiflow.data.local.dao.AnimeTracksDao
import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity.Companion.toEntity
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrack
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity.Companion.toEntity
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class AnimeTracksMediator(
    private val animeTracksRepository: AnimeTracksRepository,
    private val appRoomDatabase: AppRoomDatabase,
    private val animeTracksDao: AnimeTracksDao,
    private val userRateStatus: UserRateStatusEnum
): RemoteMediator<Int, AnimeTrack>() {

    companion object {
        private val loadedPagesMap = mutableMapOf<UserRateStatusEnum, Int>()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AnimeTrack>
    ): MediatorResult {
        return try {
            // I'm using custom page map cuz of the way the API works. It's indexed starting with 1
            // and not 0, which works... a bit weird (I tried to fix it for 3 hours) with Paging3
            // Of course I'm not sure about this conclusion
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

            val response = animeTracksRepository.getAnimeTracks(
                page = page,
                limit = state.config.pageSize,
                status = userRateStatus,
                order = UserRateOrderInputType(
                    field = UserRateOrderFieldEnum.updated_at,
                    order = SortOrderEnum.desc
                )
            )

            if (response.isSuccess) {
                val data = response.getOrThrow()
                val tracks = data.userRates.map { userRate ->
                    userRate.animeUserRateWithModel.toEntity()
                }
                val animeItems = data.userRates
                    .mapNotNull { it.animeUserRateWithModel.anime?.animeShort }
                    .map { it.toEntity() }

                appRoomDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        animeTracksDao.clearTracks(userRateStatus.name)
                        animeTracksDao.clearAnimeItems(userRateStatus.name)
                    }
                    appRoomDatabase.animeTracksDao().insertTracks(tracks)
                    appRoomDatabase.animeTracksDao().insertAnimeItems(animeItems)
                }

                val endOfPaginationReached = data.userRates.isEmpty() || data.userRates.size < state.config.pageSize
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                return MediatorResult.Error(response.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: IOException) {
            Log.e("ChatRestrictionMediator", "Error loading data: ${e.message}")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("ChatRestrictionMediator", "Error loading data: ${e.message}")
            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e("ChatRestrictionMediator", "Error loading data: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}