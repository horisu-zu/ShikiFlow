package com.example.shikiflow.presentation.viewmodel.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.MangaDetailsQuery
import com.example.shikiflow.domain.repository.MangaRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
): ViewModel() {

    private val _mangaDetails = MutableStateFlow<Resource<MangaDetailsQuery.Manga>>(Resource.Loading())
    val mangaDetails = _mangaDetails.asStateFlow()

    fun getMangaDetails(id: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if(!isRefresh) {
                _mangaDetails.value = Resource.Loading()
            }

            try {
                val result = mangaRepository.getMangaDetails(id)
                _mangaDetails.value = when {
                    result.isSuccess -> Resource.Success(result.getOrNull())
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
}