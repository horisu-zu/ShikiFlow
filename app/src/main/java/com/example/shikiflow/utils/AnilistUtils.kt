package com.example.shikiflow.utils

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation

object AnilistUtils {
    fun <T : Operation.Data> ApolloResponse<T>.toResult(): Result<T> {
        if(hasErrors()) {
            return Result.failure(Exception("Errors: $errors"))
        }

        return data?.let {
            Result.success(it)
        } ?: Result.failure(Exception(exception))
    }

    inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
        return fold(
            onSuccess = { transform(it) },
            onFailure = { Result.failure(it) }
        )
    }
}