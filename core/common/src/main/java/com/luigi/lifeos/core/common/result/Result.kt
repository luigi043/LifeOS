package com.luigi.lifeos.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Type-safe wrapper for async work. Use in ViewModels to expose loading /
 * success / error states without leaking exceptions to the UI layer.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val throwable: Throwable) : Result<Nothing>
    data object Loading : Result<Nothing>
}

/**
 * Pipe a [Flow] into a [Result] flow. Emits `Loading` first, `Success` on
 * each value, and `Error` if upstream throws (without cancelling the flow).
 *
 *  ```
 *  repository.observeBudget()
 *      .asResult()
 *      .stateIn(viewModelScope, ...)
 *  ```
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = map<T, Result<T>> { Result.Success(it) }
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(it)) }
