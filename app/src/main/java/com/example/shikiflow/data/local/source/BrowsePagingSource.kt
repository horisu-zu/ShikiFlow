package com.example.shikiflow.data.local.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.MediaDetailsDataSource
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.search.BrowseOptions
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BrowsePagingSource @Inject constructor(
    private val mediaDetailsDataSource: MediaDetailsDataSource,
    private val browseType: BrowseType?,
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
            val pageSize = params.loadSize

            if(options.name.isNullOrEmpty() && browseType == null) {
                return LoadResult.Page(emptyList(), null, null)
            }

            val response = mediaDetailsDataSource.paginatedMedia(
                page = page,
                limit = pageSize,
                search = options.name,
                mediaType = options.mediaType,
                status = options.status,
                order = options.order,
                format = options.format,
                season = options.season,
                genre = options.genre,
                score = options.score,
                rating = options.ageRating
            )

            response.fold(
                onSuccess = { result ->
                    val prevKey = if (page > 1) page - 1 else null
                    val nextKey = if (result.size == pageSize) page + 1 else null

                    LoadResult.Page(
                        data = result,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                },
                onFailure = { error ->
                    Log.d("BrowsePagingSource", "Error: ${error.message}")
                    LoadResult.Error(error)
                }
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}