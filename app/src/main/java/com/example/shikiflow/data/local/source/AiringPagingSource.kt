package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.domain.model.anime.AiringAnime
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * I know it kinda beats the purpose of pager,
 * but due to the inability to load the airings the way I do with AniList
 * well, let's say there are ~90 ongoings and I load 30 per page
 * then I sort by the airingAt/releasedOn and there are only a few per day in the page result
 * loading 2 pages per once is better UX (I think)
 */
class AiringPagingSource @Inject constructor(
    private val mediaDataSource: MediaDataSource,
    private val onList: Boolean,
    private val airingAtGreater: Long,
    private val airingAtLesser: Long
): PagingSource<Int, AiringAnime>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AiringAnime> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        return try {
            val (r1, r2) = coroutineScope {
                val p1 = async {
                    mediaDataSource.getAiringSchedule(
                        page = page,
                        limit = pageSize,
                        onList = onList,
                        airingAtGreater = airingAtGreater,
                        airingAtLesser = airingAtLesser
                    ).getOrThrow()
                }
                val p2 = async {
                    mediaDataSource.getAiringSchedule(
                        page = page + 1,
                        limit = pageSize,
                        onList = onList,
                        airingAtGreater = airingAtGreater,
                        airingAtLesser = airingAtLesser
                    ).getOrThrow()
                }

                p1.await() to p2.await()
            }

            val combined = (r1 + r2)
                .filter { airingAnime ->
                    airingAnime.airingAt?.epochSeconds in airingAtGreater..airingAtLesser ||
                    airingAnime.releasedOn?.epochSeconds in airingAtGreater..airingAtLesser
                }
                .filter { airingAnime ->
                    if (onList) airingAnime.data.userRateStatus != null else true
                }

            LoadResult.Page(
                data = combined,
                prevKey = if (page > 2) page - 2 else null,
                nextKey = if (r1.isEmpty() || r2.isEmpty()) null else page + 2
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, AiringAnime>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}