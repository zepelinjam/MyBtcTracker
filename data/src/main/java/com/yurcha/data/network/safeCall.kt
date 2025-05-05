package com.yurcha.data.network

import com.yurcha.domain.model.usecase.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <D, E> safeCall(
    call: suspend () -> D,
    onException: suspend (Throwable) -> E? = { null }
): DataResult<D, E> {
    return withContext(Dispatchers.IO) {
        try {
            DataResult.Success(call.invoke())
        } catch (e: Throwable) {
            val error = onException(e)
            if (error == null) throw e else DataResult.Error(error)
        }
    }
}

