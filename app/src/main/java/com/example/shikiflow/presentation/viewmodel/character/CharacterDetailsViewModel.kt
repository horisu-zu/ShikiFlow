package com.example.shikiflow.presentation.viewmodel.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
): ViewModel() {

    private val _characterDetails = MutableStateFlow<Resource<MediaCharacter>>(Resource.Loading())
    val characterDetails = _characterDetails.asStateFlow()

    fun getCharacterDetails(characterId: Int, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if(characterDetails.value is Resource.Success && !isRefresh) return@launch else {
                _characterDetails.value = Resource.Loading()
            }

            val result = characterRepository.getCharacterDetails(characterId)

            result.fold(
                onSuccess = { details ->
                    _characterDetails.value = Resource.Success(details)
                },
                onFailure = { exception ->
                    _characterDetails.value = Resource.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }
}