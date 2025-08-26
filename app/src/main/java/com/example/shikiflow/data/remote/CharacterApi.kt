package com.example.shikiflow.data.remote

import com.example.shikiflow.domain.model.character.ShikiCharacter
import retrofit2.http.GET
import retrofit2.http.Path

interface CharacterApi {
    @GET("/api/characters/{characterId}")
    suspend fun getCharacterDetails(
        @Path("characterId") characterId: String
    ): ShikiCharacter
}