package com.example.shikiflow.domain.repository

import com.apollographql.apollo.ApolloClient
import com.example.graphql.CurrentUserQuery
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {

    suspend fun fetchCurrentUser(): Result<CurrentUserQuery.Data> {
        return try {
            val response = apolloClient.query(CurrentUserQuery()).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.joinToString { it.message }
                    ?: "Unknown error occurred"
                Result.failure(Exception(errorMessage))
            } else {
                response.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("No data received"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}