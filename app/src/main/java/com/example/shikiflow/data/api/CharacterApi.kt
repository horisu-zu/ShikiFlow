package com.example.shikiflow.data.api

import com.example.shikiflow.data.character.ShikiCharacter
import retrofit2.http.GET
import retrofit2.http.Path

interface CharacterApi {
    @GET("/api/characters/{characterId}")
    suspend fun getCharacterDetails(
        @Path("characterId") characterId: String
    ): ShikiCharacter
}