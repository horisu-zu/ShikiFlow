package com.example.shikiflow.presentation.viewmodel.character.details

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val userRepository: UserRepository,
    settingsRepository: SettingsRepository
): UiStateViewModel<CharacterDetailsUiState>() {

    override val initialState: CharacterDetailsUiState = CharacterDetailsUiState()

    fun setCharacterId(characterId: Int) {
        mutableUiState.update { state ->
            state.copy(characterId = characterId)
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(isRefreshing = true)
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            userRepository.toggleFavorite(characterId = id).let { result ->
                if(result is DataResult.Success) {
                    mutableUiState.update { state ->
                        state.copy(
                            details = state.details?.copy(
                                isFavorite = !state.details.isFavorite!!,
                                favorites = when(state.details.isFavorite) {
                                    true -> state.details.favorites?.minus(1)
                                    false -> state.details.favorites?.plus(1)
                                }
                            )
                        )
                    }
                }
            }
        }
    }

    init {
        mutableUiState
            .filter { state ->
                state.characterId != null
            }
            .distinctUntilChanged { old, new ->
                old.characterId == new.characterId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                characterRepository.getCharacterDetails(state.characterId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when (result) {
                        is DataResult.Success -> {
                            state.copy(
                                details = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        else -> {
                            state.copy(
                                isLoading = true,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)

        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                mutableUiState.update { state ->
                    state.copy(authType = authType)
                }
            }.launchIn(viewModelScope)
    }
}