package com.example.shikiflow.presentation.viewmodel.user.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.repository.NotificationRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.screen.more.notifications.NotificationTypeGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    val notificationItems = NotificationTypeGroup.entries.associateWith { typeGroup ->
        _uiState
            .filter { state ->
                state.currentGroup == typeGroup
            }
            .distinctUntilChangedBy { state -> state.currentGroup }
            .flatMapLatest {
                notificationRepository.getNotifications(
                    typeGroup = typeGroup,
                    resetCount = typeGroup == NotificationTypeGroup.ALL
                )
            }

            .cachedIn(viewModelScope)
    }

    fun setNotificationTypeGroup(typeGroup: NotificationTypeGroup) {
        _uiState.update { state ->
            state.copy(
                currentGroup = typeGroup
            )
        }
    }

    fun resetUnreadCount() {
        viewModelScope.launch {
            settingsRepository.saveUnreadCount(0)
        }
    }
}