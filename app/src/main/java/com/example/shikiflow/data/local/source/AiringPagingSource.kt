package com.example.shikiflow.data.local.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.domain.model.anime.AiringAnime
import javax.inject.Inject

class AiringPagingSource @Inject constructor(
    private val mediaDataSource: MediaDataSource,
    private val onList: Boolean,
    private val airingAtGreater: Long,
    private val airingAtLesser: Long
): PagingSource<Int, AiringAnime>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AiringAnime> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        val response = mediaDataSource.getAiringSchedule(
            page = page,
            limit = pageSize,
            onList = onList,
            airingAtGreater = airingAtGreater,
            airingAtLesser = airingAtLesser
        )

        return response.fold(
            onSuccess = { result ->
                Log.d("AiringPagingSource", "Result Size: ${result.size}")
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (result.size < pageSize) null else page + 1

                val filteredResult = result
                    .filter { airingAnime ->
                        airingAnime.airingAt?.epochSeconds in airingAtGreater..airingAtLesser ||
                        airingAnime.releasedOn?.epochSeconds in airingAtGreater..airingAtLesser
                    }.filter { airingAnime ->
                        if (onList) airingAnime.data.userRateStatus != null
                            else true
                    }

                LoadResult.Page(
                    data = filteredResult,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            },
            onFailure = { error ->
                LoadResult.Error(error)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, AiringAnime>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}