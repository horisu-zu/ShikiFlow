package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.character.ShikiCharacter

interface CharacterRepository {
    suspend fun getCharacterDetails(
        characterId: String
    ): Result<ShikiCharacter>
}