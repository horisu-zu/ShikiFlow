package com.example.shikiflow.presentation.viewmodel.character

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.character.ShikiCharacter
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

    private var _characterId: String? = null
    private val _characterDetails = MutableStateFlow<Resource<ShikiCharacter>>(Resource.Loading())
    val characterDetails = _characterDetails.asStateFlow()

    fun getCharacterDetails(characterId: String) {
        viewModelScope.launch {
            if(characterId == _characterId) return@launch

            _characterDetails.value = Resource.Loading()
            try {
                val result = characterRepository.getCharacterDetails(characterId)
                _characterDetails.value = when {
                    result.isSuccess -> {
                        _characterId = characterId
                        Resource.Success(result.getOrNull())
                    }
                    result.isFailure -> Resource.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                    else -> Resource.Error("Unknown error")
                }
            } catch (e: Exception) {
                _characterDetails.value = Resource.Error(e.message ?: "Unknown error")
            } finally {
                Log.d("CharacterDetailsViewModel", "getCharacterDetails: ${_characterDetails.value}")
            }
        }
    }
}