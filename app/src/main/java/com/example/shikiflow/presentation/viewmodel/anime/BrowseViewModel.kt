package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.data.anime.Browse
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.local.source.BrowsePagingSource
import com.example.shikiflow.data.mapper.BrowseOptions
import com.example.shikiflow.data.mapper.BrowseParams
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _animeBrowseMap = mutableMapOf<BrowseKey, Flow<PagingData<Browse>>>()

    private data class BrowseKey(
        val type: BrowseType,
        val options: BrowseOptions,
        val name: String?
    )

    fun paginatedBrowse(
        type: BrowseType = BrowseType.AnimeBrowseType.ONGOING,
        options: BrowseOptions = BrowseParams.animeParams[type] ?: BrowseOptions(),
        name: String? = null,
    ): Flow<PagingData<Browse>> {
        val key = BrowseKey(type, options, name)

        return _animeBrowseMap.getOrPut(key) {
            Pager(
                config = PagingConfig(
                    pageSize = 45,
                    enablePlaceholders = true,
                    prefetchDistance = 12,
                    initialLoadSize = 45
                ),
                pagingSourceFactory = { BrowsePagingSource(
                    animeRepository = animeRepository,
                    mangaRepository = mangaRepository,
                    name = name,
                    type = type,
                    options = options
                ) }
            ).flow.cachedIn(viewModelScope)
        }
    }
}