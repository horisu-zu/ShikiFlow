package com.example.shikiflow.data.repository

import android.util.Log
import com.example.shikiflow.data.remote.CharacterApi
import com.example.shikiflow.domain.model.character.ShikiCharacter
import com.example.shikiflow.domain.repository.CharacterRepository
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val characterApi: CharacterApi
): CharacterRepository {
    override suspend fun getCharacterDetails(
        characterId: String
    ): Result<ShikiCharacter> {
        return try {
            val response = characterApi.getCharacterDetails(characterId)

            Result.success(response)
        } catch (e: Exception) {
            Log.e("CharacterRepository", "Error: ${e.message}")
            Result.failure(e)
        }
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
}