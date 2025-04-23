package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.anime.Browse
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.anime.toBrowseAnime
import com.example.shikiflow.data.anime.toBrowseManga
import com.example.shikiflow.data.mapper.BrowseOptions
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.MangaRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BrowsePagingSource @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
    private val name: String?,
    private val type: BrowseType,
    private val options: BrowseOptions
): PagingSource<Int, Browse>() {

    override fun getRefreshKey(state: PagingState<Int, Browse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Browse> {
        return try {
            val page = params.key ?: 1

            when(type) {
                is BrowseType.AnimeBrowseType -> {
                    val response = animeRepository.browseAnime(
                        name = name,
                        page = page,
                        limit = params.loadSize,
                        searchInUserList = false,
                        status = options.status?.name,
                        order = options.order,
                        kind = options.kind?.name,
                        season = options.season,
                        genre = options.genre,
                        userStatus = options.userListStatus
                    )

                    if (response.isSuccess) {
                        val data = response.getOrNull()?.map { anime ->
                            anime.toBrowseAnime()
                        } ?: emptyList()

                        val prevKey = if (page > 0) page - 1 else null
                        val nextKey = if (data.isNotEmpty()) page + 1 else null

                        LoadResult.Page(
                            data = data,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    } else {
                        LoadResult.Error(response.exceptionOrNull() ?: Exception("Unknown error"))
                    }
                }
                is BrowseType.MangaBrowseType -> {
                    val response = mangaRepository.browseManga(
                        name = name,
                        page = page,
                        limit = params.loadSize,
                        searchInUserList = false,
                        status = options.status?.name,
                        order = options.order,
                        kind = options.kind?.name,
                        genre = options.genre
                    )

                    if (response.isSuccess) {
                        val data = response.getOrNull()?.map { manga ->
                            manga.toBrowseManga()
                        } ?: emptyList()

                        val prevKey = if (page > 0) page - 1 else null
                        val nextKey = if (data.isNotEmpty()) page + 1 else null

                        LoadResult.Page(
                            data = data,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    } else {
                        LoadResult.Error(response.exceptionOrNull() ?: Exception("Unknown error"))
                    }
                }
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}