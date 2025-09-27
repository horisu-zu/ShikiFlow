package com.example.shikiflow.data.remote

import com.example.shikiflow.domain.model.person.ShikiPerson
import retrofit2.http.GET
import retrofit2.http.Path

interface PersonApi {
    @GET("/api/people/{id}")
    suspend fun getPersonDetails(
        @Path("id") id: String
    ): ShikiPerson
}