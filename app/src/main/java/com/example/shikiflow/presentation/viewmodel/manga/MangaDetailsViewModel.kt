package com.example.shikiflow.presentation.viewmodel.manga

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.MangaDetailsQuery
import com.example.shikiflow.domain.repository.MangaRepository
import com.example.shikiflow.domain.usecase.GetMangaDexUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val getMangaDexUseCase: GetMangaDexUseCase
): ViewModel() {

    private var currentId: String? = null

    private val _mangaDetails = MutableStateFlow<Resource<MangaDetailsQuery.Manga>>(Resource.Loading())
    val mangaDetails = _mangaDetails.asStateFlow()

    private val _mangaDexIds = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val mangaDexIds = _mangaDexIds.asStateFlow()

    fun getMangaDetails(id: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if(!isRefresh && currentId != id) {
                _mangaDetails.value = Resource.Loading()
            }

            try {
                val result = mangaRepository.getMangaDetails(id)
                _mangaDetails.value = when {
                    result.isSuccess -> {
                        currentId = id

                        getMangaDexId(
                            title = result.getOrNull()?.japanese ?: "",
                            malId = result.getOrNull()?.malId ?: ""
                        )

                        Resource.Success(result.getOrNull())
                    }
                    result.isFailure -> Resource.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error"
                    )
                    else -> Resource.Error("Unknown error")
                }
            } catch (e: Exception) {
                _mangaDetails.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun getMangaDexId(title: String, malId: String) {
        viewModelScope.launch {
            Log.d("MangaDetailsViewModel", "Fetching MangaDex ID for title: $title, MAL ID: $malId")
            _mangaDexIds.value = getMangaDexUseCase(title, malId)
            Log.d("MangaDetailsViewModel", "MangaDex ID: ${_mangaDexIds.value.data}")
        }
    }
}