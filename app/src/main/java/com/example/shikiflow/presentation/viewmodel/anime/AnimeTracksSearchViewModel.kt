package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.graphql.AnimeBrowseQuery
import com.example.shikiflow.data.local.source.TracksPagingSource
import com.example.shikiflow.domain.model.anime.MyListString
import com.example.shikiflow.domain.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AnimeTracksSearchViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
): ViewModel() {
    private var cachedFlow: Flow<PagingData<AnimeBrowseQuery.Anime>>? = null
    private var cachedParams: Pair<String, MyListString?>? = null

    fun getPaginatedTracks(
        title: String,
        userStatus: MyListString?
    ): Flow<PagingData<AnimeBrowseQuery.Anime>> {
        val currentParams = title to userStatus

        if (cachedParams == currentParams && cachedFlow != null) {
            return cachedFlow!!
        }

        val paginatedData = Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                TracksPagingSource(
                    animeRepository = animeRepository,
                    userStatus = userStatus,
                    title = title
                )
            }
        ).flow.cachedIn(viewModelScope)

        cachedFlow = paginatedData
        cachedParams = currentParams

        return paginatedData
    }
}