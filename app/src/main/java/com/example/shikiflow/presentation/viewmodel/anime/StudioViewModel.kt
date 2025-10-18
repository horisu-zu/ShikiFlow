package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.data.local.source.BrowsePagingSource
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.mapper.BrowseOptions
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import kotlin.collections.set

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class StudioViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository
): ViewModel() {

    private val _titleQuery = MutableStateFlow("")
    val titleQuery = _titleQuery.asStateFlow()

    private var _cachedTitle: String? = null
    private var _studioCache = mutableMapOf<String, Flow<PagingData<Browse>>>()

    fun getStudioAnime(
        studioId: String
    ): Flow<PagingData<Browse>> {
        val pagerFlow = {
            _titleQuery
                .debounce(500L)
                .distinctUntilChanged()
                .flatMapLatest { title ->
                    _cachedTitle = title
                    Pager(
                        config = PagingConfig(
                            pageSize = 30,
                            enablePlaceholders = true,
                            prefetchDistance = 9,
                            initialLoadSize = 30
                        ),
                        pagingSourceFactory = {
                            BrowsePagingSource(
                                animeRepository = animeRepository,
                                mangaRepository = mangaRepository,
                                type = BrowseType.AnimeBrowseType.SEARCH,
                                options = BrowseOptions(
                                    studio = studioId,
                                    name = title
                                ),
                            )
                        }
                    ).flow
                }.cachedIn(viewModelScope)
        }

        return if(_cachedTitle != _titleQuery.value) {
            _cachedTitle = _titleQuery.value
            pagerFlow().also { _studioCache[studioId] = it }
        } else {
            _studioCache.getValue(studioId)
        }
    }

    fun updateTitleQuery(newQuery: String) {
        _titleQuery.value = newQuery
    }
}