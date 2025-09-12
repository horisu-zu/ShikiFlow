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

    private var currentId: String? = null

    private val _animeDetails = MutableStateFlow<Resource<AnimeDetailsQuery.Anime>>(Resource.Loading())
    val animeDetails = _animeDetails.asStateFlow()

    fun getAnimeDetails(id: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh && currentId != id) {
                _animeDetails.value = Resource.Loading()
            }

            try {
                val result = animeRepository.getAnimeDetails(id)

                result?.let { animeDetails ->
                    _animeDetails.value = Resource.Success(animeDetails)
                    currentId = id
                }
            } catch (e: Exception) {
                _animeDetails.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
}