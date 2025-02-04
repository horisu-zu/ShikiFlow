package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.AnimeDetailsQuery
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val _animeDetails = MutableStateFlow<Resource<AnimeDetailsQuery.Anime>>(Resource.Loading())
    val animeDetails = _animeDetails.asStateFlow()

    fun getAnimeDetails(id: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) {
                _animeDetails.value = Resource.Loading()
            }

            try {
                val result = animeRepository.getAnimeDetails(id)
                _animeDetails.value = when {
                    result.isSuccess -> Resource.Success(result.getOrNull())
                    result.isFailure -> Resource.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error"
                    )
                    else -> Resource.Error("Unknown error")
                }
            } catch (e: Exception) {
                _animeDetails.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
}