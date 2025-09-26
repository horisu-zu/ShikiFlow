package com.example.shikiflow.data.repository

import com.example.shikiflow.data.remote.CharacterApi
import com.example.shikiflow.domain.model.character.ShikiCharacter
import com.example.shikiflow.domain.repository.CharacterRepository
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val characterApi: CharacterApi
): CharacterRepository {
    override suspend fun getCharacterDetails(
        characterId: String
    ): ShikiCharacter = characterApi.getCharacterDetails(characterId)
        /*val query = CharacterDetailsQuery(
            ids = Optional.presentIfNotNull(listOf(characterId))
        )

        return try {
            val response = apolloClient.query(query).execute()

            response.data?.let { charactersResponse ->
                Result.success(charactersResponse.characters.first())
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }*/
}