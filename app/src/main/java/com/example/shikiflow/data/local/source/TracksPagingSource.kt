package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TracksPagingSource @Inject constructor(
    private val mediaTracksDataSource: MediaTracksDataSource,
    private val userId: Int?,
    private val title: String,
    private val userStatus: UserRateStatus?
): PagingSource<Int, AnimeTrack>() {

    override fun getRefreshKey(state: PagingState<Int, AnimeTrack>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeTrack> {

        if(title.isEmpty()) return LoadResult.Page(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )

        return try {
            val page = params.key ?: 1

            val response = mediaTracksDataSource.browseAnimeTracks(
                userId = userId,
                name = title,
                userStatus = userStatus,
                page = page,
                limit = params.loadSize
            )

            if (response.isSuccess) {
                val data = response.getOrNull() ?: emptyList()

                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (data.size == params.loadSize) page + 1 else null

                LoadResult.Page(
                    data = data,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(response.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}