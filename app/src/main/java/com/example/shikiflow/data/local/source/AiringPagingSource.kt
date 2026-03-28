package com.example.shikiflow.data.local.source

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
    private val distinctIds = mutableSetOf<Int>()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AiringAnime> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        val response = mediaDataSource.getAiringSchedule(
            page = page,
            limit = pageSize,
            airingAtGreater = airingAtGreater,
            airingAtLesser = airingAtLesser
        )

        return response.fold(
            onSuccess = { result ->
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (result.size >= pageSize) page + 1 else null

                val filteredResult = result
                    .filter { airingAnime ->
                        distinctIds.add(airingAnime.data.id)
                    }
                    .filter { airingAnime ->
                        if (onList) airingAnime.data.userRateStatus != null
                            else true
                    }
                    //Filtering here because Shikimori API does not have the functionality
                    //to return the data within the airingAt values
                    .filter { airingAnime ->
                        airingAnime.airingAt?.epochSeconds in airingAtGreater..airingAtLesser ||
                        airingAnime.releasedOn?.epochSeconds in airingAtGreater..airingAtLesser
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