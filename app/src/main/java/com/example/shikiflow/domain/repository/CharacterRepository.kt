package com.example.shikiflow.domain.repository

import android.util.Log
import com.example.shikiflow.data.character.ShikiCharacter
import com.example.shikiflow.di.api.CharacterApi
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val characterApi: CharacterApi
) {

    suspend fun getCharacterDetails(
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