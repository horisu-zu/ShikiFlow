package com.example.shikiflow.presentation.viewmodel.browse.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.utils.DateUtils.thisWeekdayTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DayOfWeek
import javax.inject.Inject
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OngoingsCalendarViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _params = MutableStateFlow(OngoingsCalendarParams())
    val params = _params.asStateFlow()

    private val now = Clock.System.now()

    val calendarItems = DayOfWeek.entries.associateWith { dayOfWeek ->
        _params.filter { params ->
            params.currentDay != null && dayOfWeek == params.currentDay
        }
            .distinctUntilChanged()
            .flatMapLatest { params ->
                val start = now.thisWeekdayTimestamp(
                    dayOfWeek = params.currentDay!!,
                    isEndOfDay = false
                )
                val end = now.thisWeekdayTimestamp(
                    dayOfWeek = params.currentDay,
                    isEndOfDay = true
                )

                mediaRepository.getAiringAnimes(
                    onList = params.onList,
                    airingAtGreater = start,
                    airingAtLesser = end
                )
            }.cachedIn(viewModelScope)
    }

    fun setCurrentDay(currentDay: DayOfWeek) {
        _params.update { params ->
            params.copy(currentDay = currentDay)
        }
    }

    fun setOnList(onList: Boolean) {
        _params.update { params ->
            params.copy(onList = onList)
        }
    }
}