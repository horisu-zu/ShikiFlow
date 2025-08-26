package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.common.ExternalLink
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.usecase.GetExternalLinksUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExternalLinksViewModel @Inject constructor(
    private val getExternalLinksUseCase: GetExternalLinksUseCase
) : ViewModel() {

    private var currentMediaId: String? = null
    private val _externalLinks = MutableStateFlow<Resource<List<ExternalLink>>>(Resource.Loading())
    val externalLinks = _externalLinks.asStateFlow()

    fun getExternalLinks(id: String, mediaType: MediaType) {
        viewModelScope.launch {
            if(currentMediaId == id) { return@launch }

            getExternalLinksUseCase(id, mediaType).collect { result ->
                _externalLinks.value = result
                when (result) {
                    is Resource.Success -> {
                        Log.d("ExternalLinksViewModel", "Links fetched successfully: ${result.data}")
                        currentMediaId = id
                    }
                    is Resource.Error -> {
                        Log.d("ExternalLinksViewModel", "Error fetching links: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d("ExternalLinksViewModel", "Loading links...")
                    }
                }
            }
        }
    }
}