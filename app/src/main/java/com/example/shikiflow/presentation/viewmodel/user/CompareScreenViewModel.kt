package com.example.shikiflow.presentation.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.userrate.ComparisonType
import com.example.shikiflow.domain.model.userrate.MediaComparison
import com.example.shikiflow.domain.usecase.GroupUserRatesUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareScreenViewModel @Inject constructor(
    private val groupUserRatesUseCase: GroupUserRatesUseCase
): ViewModel() {

    private val _targetUserId = mutableMapOf<MediaType, String>()
    private val _userRates = MutableStateFlow<Map<MediaType, Resource<Map<ComparisonType, List<MediaComparison>>>>>(
        mapOf(
            MediaType.ANIME to Resource.Loading(),
            MediaType.MANGA to Resource.Loading()
        )
    )
    val userRates = _userRates.asStateFlow()

    fun compareUserRates(
        currentUserId: String,
        targetUserId: String,
        mediaType: MediaType
    ) {
        viewModelScope.launch {
            if(_targetUserId[mediaType] == targetUserId) {
                return@launch
            } else {
                _userRates.update { currentMap ->
                    currentMap.toMutableMap().apply {
                        put(mediaType, Resource.Loading())
                    }
                }
            }

            val response = groupUserRatesUseCase(currentUserId, targetUserId, mediaType)

            _userRates.update { currentMap ->
                currentMap.toMutableMap().apply {
                    put(mediaType, response)
                }
            }

            when(response) {
                is Resource.Loading -> {
                    Log.d("CompareScreenViewModel", "Loading user rates comparison")
                }
                is Resource.Success -> {
                    _targetUserId[mediaType] = targetUserId
                    Log.d("CompareScreenViewModel", "Results received")
                }
                is Resource.Error -> {
                    Log.e("CompareScreenViewModel", "Error comparing user rates: ${response.message}")
                }
            }
        }
    }
}