package com.example.shikiflow.presentation.viewmodel.character.details

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.presentation.UiState

data class CharacterDetailsUiState(
    val characterId: Int? = null,
    val authType: AuthType? = null,
    val details: MediaCharacter? = null,

    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}