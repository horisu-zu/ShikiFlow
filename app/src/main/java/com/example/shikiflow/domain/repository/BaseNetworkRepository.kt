package com.example.shikiflow.domain.repository

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.CacheMissException
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

abstract class BaseNetworkRepository {
    fun <D : Operation.Data, R> Flow<ApolloResponse<D>>.asDataResult(
        transform: (D) -> R
    ): Flow<DataResult<R>> = this
        .filter { it.exception !is CacheMissException } //Don't want to manually deal with the Fetch Policy
        .map { response ->
            when {
                response.data != null -> DataResult.Success(transform(response.data!!))

                response.hasErrors() -> {
                    val errorString = response.errors?.joinToString { it.message } ?: "Unknown Error"
                    DataResult.Error(message = errorString)
                }

                response.exception != null -> {
                    val errorMessage = response.exception?.message ?: "Unknown Error"
                    DataResult.Error(message = errorMessage)
                }

                else -> DataResult.Loading
            }
        }.onStart { emit(DataResult.Loading) }
}