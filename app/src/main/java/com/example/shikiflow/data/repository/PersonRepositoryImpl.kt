package com.example.shikiflow.data.repository

import com.example.shikiflow.data.remote.PersonApi
import com.example.shikiflow.domain.model.person.ShikiPerson
import com.example.shikiflow.domain.repository.PersonRepository
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val personApi: PersonApi
) : PersonRepository {
    override suspend fun getPersonDetails(id: String): ShikiPerson = personApi.getPersonDetails(id)
}