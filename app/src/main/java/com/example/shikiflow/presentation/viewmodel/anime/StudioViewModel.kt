package com.example.shikiflow.presentation.viewmodel.anime

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.track.OrderOption
import com.example.shikiflow.domain.repository.MediaRepository
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
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _titleQuery = MutableStateFlow("")
    val titleQuery = _titleQuery.asStateFlow()

    private var onUserList = mutableStateOf<Boolean?>(null)

    private var _cachedTitle: String? = null
    private var _studioCache = mutableMapOf<Int, Flow<PagingData<Browse>>>()

    fun getStudioAnime(
        studioId: Int,
        orderOption: OrderOption
    ): Flow<PagingData<Browse>> {
        val pagerFlow = {
            _titleQuery
                .debounce(500L)
                .distinctUntilChanged()
                .flatMapLatest { title ->
                    _cachedTitle = title
                    mediaRepository.getStudioMedia(
                        studioId,
                        search = title,
                        order = orderOption,
                        onList = onUserList.value
                    )
                }.cachedIn(viewModelScope)
        }

        return if(_cachedTitle != _titleQuery.value) {
            _cachedTitle = _titleQuery.value
            pagerFlow().also { _studioCache[studioId] = it }
        } else {
            _studioCache.getValue(studioId)
        }
    }

    fun onUserListSearchChange(value: Boolean) {
        onUserList.value = value
    }

    fun updateTitleQuery(newQuery: String) {
        _titleQuery.value = newQuery
    }
}