package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.person.ShikiPerson

interface PersonRepository {
    suspend fun getPersonDetails(id: String): ShikiPerson
}