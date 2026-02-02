package com.example.shikiflow.presentation.viewmodel.person

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.repository.PersonRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val personRepository: PersonRepository
): ViewModel() {

    private var _currentId: Int? = null
    private val _personDetails = MutableStateFlow<Resource<StaffDetails>>(Resource.Loading())
    val personDetails = _personDetails.asStateFlow()

    fun getPersonDetails(id: Int, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (_currentId == id && !isRefresh) {
                return@launch
            } else {
                _personDetails.value = Resource.Loading()
            }

            val result = personRepository.getStaffDetails(id)

            result.fold(
                onSuccess = { details ->
                    _personDetails.value = Resource.Success(details)
                    _currentId = id
                },
                onFailure = { exception ->
                    Log.e("PersonViewModel", "Error fetching person details: ${exception.message}")
                    _personDetails.value = Resource.Error(exception.localizedMessage ?: "Unknown error")
                }
            )
        }
    }
}