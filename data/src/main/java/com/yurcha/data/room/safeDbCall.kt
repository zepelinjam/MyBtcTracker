package com.yurcha.data.room

import com.yurcha.domain.model.usecase.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <D, E> safeDbCall(apiCall: suspend () -> D): DataResult<D, Nothing?> {
    return withContext(Dispatchers.IO) {
        try {
            DataResult.Success(apiCall.invoke())
        } catch (e: Exception) {
            DataResult.Error(null) // або створити власний тип помилки
        }
    }
}
