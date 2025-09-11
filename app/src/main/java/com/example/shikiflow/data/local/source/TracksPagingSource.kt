package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.type.OrderEnum
import com.example.shikiflow.domain.model.anime.MyListString
import com.example.shikiflow.domain.repository.AnimeRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.isNotEmpty

class TracksPagingSource @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val title: String,
    private val userStatus: MyListString?
): PagingSource<Int, AnimeBrowseQuery.Anime>() {

    override fun getRefreshKey(state: PagingState<Int, AnimeBrowseQuery.Anime>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeBrowseQuery.Anime> {

        if(title.isEmpty()) return LoadResult.Page(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )

        return try {
            val page = params.key ?: 1

            val response = animeRepository.browseAnime(
                name = title,
                userStatus = listOf(userStatus),
                page = page,
                limit = params.loadSize,
                order = OrderEnum.popularity
            )

            if (response.isSuccess) {
                val data = response.getOrNull() ?: emptyList()

                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (data.isNotEmpty()) page + 1 else null

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