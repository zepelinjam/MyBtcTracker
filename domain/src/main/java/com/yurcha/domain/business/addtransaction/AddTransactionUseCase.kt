package com.yurcha.domain.business.addtransaction

import com.yurcha.domain.business.base.FlowUseCase
import com.yurcha.domain.di.IoDispatcher
import com.yurcha.domain.model.Transaction
import com.yurcha.domain.model.TransactionCategory
import com.yurcha.domain.model.usecase.DataResult
import com.yurcha.domain.model.usecase.Result
import com.yurcha.domain.repository.TransactionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class AddTransactionUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Transaction, AddTransactionUseCase.AddTransactionDataSuccess, AddTransactionUseCase.AddTransactionDataErrors>(dispatcher) {

    sealed class AddTransactionDataErrors {
        data object Error : AddTransactionDataErrors()
    }

    sealed class AddTransactionDataSuccess {
        data object TransactionAdded : AddTransactionDataSuccess()
    }

    override fun execute(parameters: Transaction): Flow<Result<AddTransactionDataSuccess, AddTransactionDataErrors>> {
        return flow {
            emit(Result.Loading)

            when (val transactionsResult = transactionsRepository.addNewTransaction(parameters)) {
                is DataResult.Error -> when (transactionsResult.error) {
                    TransactionsRepository.TransactionsRepositoryError.UnknownError ->
                        emit(Result.BusinessRuleError(AddTransactionDataErrors.Error))
                }

                is DataResult.Success -> emit(
                    Result.Success(AddTransactionDataSuccess.TransactionAdded)
                )
            }
        }
    }
}
