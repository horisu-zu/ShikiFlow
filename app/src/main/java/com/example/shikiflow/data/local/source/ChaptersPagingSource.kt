package com.example.shikiflow.data.local.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.mapper.MangaDexMapper.toDomain
import com.example.shikiflow.data.remote.MangaDexApi
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.MangaChapterMetadata
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ChaptersPagingSource @Inject constructor(
    private val mangaDexApi: MangaDexApi,
    private val mangaId: String,
    private val groupIds: List<String>,
    private val uploader: String?
): PagingSource<Int, MangaChapterMetadata>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MangaChapterMetadata> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            val result = mangaDexApi.getGroupChaptersList(
                limit = pageSize,
                offset = pageSize * page,
                mangaId = mangaId,
                scanlationGroups = groupIds,
                uploader = uploader
            )

            val prevKey = if (page > 0) page - 1 else null
            val nextKey = if (result.chaptersData.size == pageSize) page + 1 else null

            Log.d("ChapterPagingSource", "Result Size: ${result.chaptersData.size}")

            LoadResult.Page(
                data = result.chaptersData.map { it.toDomain() },
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MangaChapterMetadata>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}