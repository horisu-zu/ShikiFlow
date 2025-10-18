package com.example.shikiflow.data.local.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.anime.toBrowseAnime
import com.example.shikiflow.domain.model.anime.toBrowseManga
import com.example.shikiflow.domain.model.mapper.BrowseOptions
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.MangaRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BrowsePagingSource @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
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

            if(
                options.name.isNullOrEmpty() && options.studio.isNullOrEmpty()
                && type in setOf(BrowseType.AnimeBrowseType.SEARCH, BrowseType.MangaBrowseType.SEARCH)
            ) {
                return LoadResult.Page(emptyList(), null, null)
            }

            when(type) {
                is BrowseType.AnimeBrowseType -> {
                    val response = animeRepository.browseAnime(
                        name = options.name,
                        page = page,
                        limit = params.loadSize,
                        searchInUserList = !options.userListStatus.isEmpty(),
                        status = options.status?.name,
                        order = options.order,
                        kind = options.kind?.name,
                        season = options.season,
                        genre = options.genre,
                        userStatus = options.userListStatus,
                        studio = options.studio
                    )

                    response.fold(
                        onSuccess = { data ->
                            Log.d("BrowsePagingSource", "Results: $data")
                            val data = data.map { anime ->
                                anime.toBrowseAnime()
                            }

                            val prevKey = if (page > 1) page - 1 else null
                            val nextKey = if (data.isNotEmpty()) page + 1 else null

                            LoadResult.Page(
                                data = data,
                                prevKey = prevKey,
                                nextKey = nextKey
                            )
                        },
                        onFailure = { error ->
                            Log.d("BrowsePagingSource", "Error: ${error.message}")
                            LoadResult.Error(error)
                        }
                    )
                }
                is BrowseType.MangaBrowseType -> {
                    val response = mangaRepository.browseManga(
                        name = options.name,
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
                }
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}