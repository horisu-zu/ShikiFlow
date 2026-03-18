package com.example.shikiflow.utils

import android.util.Log
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface DataResult<out T> {
    data object Loading : DataResult<Nothing>

    data class Error<T>(
        val message: String
    ) : DataResult<T>

    data class Success<T>(
        val data: T
    ) : DataResult<T>

    companion object {
        fun <T : Operation.Data, R> Flow<ApolloResponse<T>>.asDataResult(
            transform: (T) -> R
        ): Flow<DataResult<R>> = this
            .map<ApolloResponse<T>, DataResult<R>> { response ->
                try {
                    Success(transform(response.dataOrThrow()))
                } catch (e: Exception) {
                    Log.d("DataResult", "Error", e)
                    Error(e.message ?: "")
                }
            }
            .onStart {
                emit(Loading)
            }
            .catch { e ->
                Log.d("DataResult", "Error", e)
                emit(Error(e.message ?: ""))
            }
    }
}