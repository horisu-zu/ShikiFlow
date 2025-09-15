package com.example.shikiflow.presentation.viewmodel.manga.read

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.usecase.AggregateMangaUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MangaChaptersViewModel @Inject constructor(
    private val aggregateMangaUseCase: AggregateMangaUseCase
): ViewModel() {

    private var currentId: String? = null
    private val _mangaChapters =
        MutableStateFlow<Resource<Map<String, List<String>>>>(Resource.Loading())
    val mangaChapters = _mangaChapters.asStateFlow()

    fun getMangaChapters(mangaDexId: String) {
        if(currentId == mangaDexId) return

        aggregateMangaUseCase(mangaDexId).onEach { result ->
            _mangaChapters.value = result
            when (result) {
                is Resource.Loading -> {
                    Log.d("MangaReadViewModel", "Aggregation in progress...")
                }
                is Resource.Success -> {
                    currentId = mangaDexId
                    Log.d("MangaReadViewModel", "Aggregation successful: ${result.data}")
                }
                is Resource.Error -> {
                    Log.e("MangaReadViewModel", "Aggregation failed: ${result.message}")
                }
            }
        }.launchIn(viewModelScope)
    }
}