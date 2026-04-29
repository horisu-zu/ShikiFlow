package com.example.shikiflow.data.local.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.apollographql.apollo.exception.ApolloHttpException
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.entity.mediatrack.MediaTrackDto
import com.example.shikiflow.data.mapper.local.MediaShortMapper.toEntity
import com.example.shikiflow.data.mapper.local.MediaTrackMapper.toEntity
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.tracks.MediaType
import org.json.JSONObject

@OptIn(ExperimentalPagingApi::class)
class MediaTracksMediator(
    private val mediaTracksDataSource: MediaTracksDataSource,
    private val appRoomDatabase: AppRoomDatabase,
    private val userRateStatus: UserRateStatus,
    private val userId: Int?,
    private val mediaType: MediaType
): RemoteMediator<Int, MediaTrackDto>() {
    private val mediaTracksDao = appRoomDatabase.mediaTracksDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MediaTrackDto>
    ): MediatorResult {
        val page = when(loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                state.lastItemOrNull() ?: return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                val loadedItemsCount = state.pages.sumOf { it.data.size }
                val nextPage = (loadedItemsCount / state.config.pageSize) + 1

                nextPage
            }
        }

        val response = mediaTracksDataSource.getMediaTracks(
            page = page,
            limit = state.config.pageSize,
            mediaType = mediaType,
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
                    userRate.track.toEntity()
                }
                val items = data.map { userRate ->
                    userRate.shortData.toEntity()
                }

                appRoomDatabase.withTransaction {
                    if(loadType == LoadType.REFRESH) {
                        mediaTracksDao.clearTracksByStatus(userRateStatus.name, mediaType)
                        mediaTracksDao.clearItemsByStatus(userRateStatus.name, mediaType)
                    }

                    mediaTracksDao.insertTracks(tracks)
                    mediaTracksDao.insertItems(items)
                }

                val endOfPaginationReached = data.isEmpty() || data.size < state.config.pageSize
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            },
            onFailure = { throwable ->
                val error = when (throwable) {
                    is ApolloHttpException -> {
                        val body = throwable.body?.readUtf8().orEmpty()

                        val errorMessage = runCatching {
                            JSONObject(body)
                                .getJSONArray("errors")
                                .getJSONObject(0)
                                .getString("message")
                        }.getOrElse { throwable.message ?: "Unknown Error" }

                        Exception(errorMessage)
                    }
                    else -> {
                        Log.e("MediaTracksMediator", "Exception: $throwable")
                        throwable as? Exception ?: Exception(throwable.message)
                    }
                }
                MediatorResult.Error(error)
            }
        )
    }
}