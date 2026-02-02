package com.example.shikiflow.data.local.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriCharactersDataSource
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.tracks.MediaType

class CharactersPagingSource(
    private val charactersDataSource: CharactersDataSource,
    private val mediaId: Int,
    private val mediaType: MediaType
): PagingSource<Int, MediaCharacterShort>() {
    override fun getRefreshKey(state: PagingState<Int, MediaCharacterShort>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaCharacterShort> {
        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        val response = charactersDataSource.loadMediaCharacters(currentPage, pageSize, mediaId, mediaType)

        return response.fold(
            onSuccess = { data ->
                LoadResult.Page(
                    data = data,
                    prevKey = if (currentPage > 1) currentPage - 1 else null,
                    nextKey = if (data.size != pageSize) null else currentPage + 1
                )
            },
            onFailure = { exception ->
                Log.d("CharactersPagingSource", "Error: $exception")
                LoadResult.Error(exception)
            }
        )
    }
}