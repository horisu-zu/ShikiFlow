package com.example.shikiflow.presentation.viewmodel.manga.read

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChapterNavigationViewModel @Inject constructor(): ViewModel() {

    private val _isNavigationVisible = MutableStateFlow(false)
    val isNavigationVisible = _isNavigationVisible.asStateFlow()

    private val _isUserInteracting = MutableStateFlow(false)

    private var hideJob: Job? = null
    private val hideDelayMs = 2000L

    fun onScrollDetected() {
        if (!_isUserInteracting.value) {
            showNavigation()
            scheduleHide()
        }
    }

    fun onUserInteractionStart() {
        _isUserInteracting.value = true
        showNavigation()
    }

    fun onUserInteractionEnd() {
        _isUserInteracting.value = false
        scheduleHide()
    }

    private fun showNavigation() {
        hideJob?.cancel()
        _isNavigationVisible.value = true
    }

    private fun scheduleHide() {
        hideJob?.cancel()
        hideJob = viewModelScope.launch {
            delay(hideDelayMs)
            if (!_isUserInteracting.value) {
                _isNavigationVisible.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        hideJob?.cancel()
    }
}