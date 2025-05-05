package com.yurcha.domain.business.base

import android.util.Log
import com.yurcha.domain.error.mapToAppError
import com.yurcha.domain.model.usecase.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

/**
 * Abstract class representing a more complex use case. A Use Case is a class that represents a
 * single task that the user can perform. It is a simple class that receives a request and returns
 * a response.
 */
abstract class FlowUseCase<in Parameters, Success, BusinessRuleError>(private val dispatcher: CoroutineDispatcher) {

    operator fun invoke(parameters: Parameters): Flow<Result<Success, BusinessRuleError>> {
        return execute(parameters)
            .catch { e ->
                Log.e("FlowUseCase", "An error occurred while executing the use case", e)
                emit(Result.Error(e.mapToAppError()))
            }
            .flowOn(dispatcher)
    }

    abstract fun execute(parameters: Parameters): Flow<Result<Success, BusinessRuleError>>
}
