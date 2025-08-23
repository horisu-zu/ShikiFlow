package com.example.shikiflow.presentation.viewmodel.manga.read

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.mangadex.manga.MangaData
import com.example.shikiflow.domain.usecase.GetMangaDexUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MangaSelectionViewModel @Inject constructor(
    private val getMangaDexUseCase: GetMangaDexUseCase
): ViewModel() {

    private var currentIds: List<String> = emptyList()
    private val _mangaList = MutableStateFlow<Resource<List<MangaData>>>(Resource.Loading())
    val mangaList = _mangaList.asStateFlow()

    fun fetchMangaByIds(mangaDexIds: List<String>) {
        if(currentIds == mangaDexIds) return

        getMangaDexUseCase(mangaDexIds).onEach { result ->
            _mangaList.value = result
            when(result) {
                is Resource.Loading -> {
                    currentIds = mangaDexIds
                    Log.d("MangaSelectionScreen", "Loading manga for IDs: $mangaDexIds")
                }
                is Resource.Success -> {
                    Log.d("MangaSelectionScreen", "Manga fetched: ${result.data}")
                }
                is Resource.Error -> {
                    Log.d("MangaSelectionScreen", "Error: ${result.message}")
                }
            }
        }.launchIn(viewModelScope)
    }
}