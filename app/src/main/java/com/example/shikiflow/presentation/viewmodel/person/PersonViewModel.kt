package com.example.shikiflow.presentation.viewmodel.person

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.person.ShikiPerson
import com.example.shikiflow.domain.repository.PersonRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val personRepository: PersonRepository
): ViewModel() {

    private var _currentId: String? = null
    private val _personDetails = MutableStateFlow<Resource<ShikiPerson>>(Resource.Loading())
    val personDetails = _personDetails.asStateFlow()

    fun getPersonDetails(id: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if(_currentId == id && !isRefresh) { return@launch } else {
                _personDetails.value = Resource.Loading()
            }

            try {
                val result = personRepository.getPersonDetails(id)

                _personDetails.value = Resource.Success(result)
                _currentId = id
            } catch (e: Exception) {
                Log.e("PersonViewModel", "Error fetching person details: ${e.message}")
                _personDetails.value = Resource.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}