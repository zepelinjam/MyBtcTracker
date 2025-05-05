package com.yurcha.domain.business.main.transaction

import com.yurcha.domain.business.base.FlowUseCase
import com.yurcha.domain.di.IoDispatcher
import com.yurcha.domain.model.Transaction
import com.yurcha.domain.model.usecase.DataResult
import com.yurcha.domain.model.usecase.Result
import com.yurcha.domain.repository.TransactionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTransactionsUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Int, GetTransactionsUseCase.GetTransactionsSuccess, GetTransactionsUseCase.GetTransactionsErrors>(dispatcher) {

    sealed class GetTransactionsErrors {
        data object NoTransactions : GetTransactionsErrors()
    }

    sealed class GetTransactionsSuccess {
        data class TransactionsData(val transactions: List<Transaction>) : GetTransactionsSuccess()
    }

    override fun execute(parameters: Int): Flow<Result<GetTransactionsSuccess, GetTransactionsErrors>> {
        return flow {
            emit(Result.Loading)

            val transactionsResult = transactionsRepository.getTransactionsList(offset = parameters)

            when (transactionsResult) {
                is DataResult.Error -> when (transactionsResult.error) {
                    TransactionsRepository.TransactionsRepositoryError.UnknownError ->
                        emit(Result.BusinessRuleError(GetTransactionsErrors.NoTransactions))
                }

                is DataResult.Success -> emit(
                    Result.Success(
                        GetTransactionsSuccess.TransactionsData(transactionsResult.data)
                    )
                )
            }
        }
    }
}