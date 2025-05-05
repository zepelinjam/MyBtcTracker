package com.yurcha.domain.business.main.balance

import com.yurcha.domain.business.base.FlowUseCase
import com.yurcha.domain.di.IoDispatcher
import com.yurcha.domain.model.usecase.DataResult
import com.yurcha.domain.model.usecase.Result
import com.yurcha.domain.repository.TransactionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetBalanceUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, GetBalanceUseCase.GetBalanceSuccess, GetBalanceUseCase.GetBalanceErrors>(dispatcher) {

    sealed class GetBalanceErrors {
        data object NoBalance : GetBalanceErrors()
    }

    sealed class GetBalanceSuccess {
        data class CurrentBalance(val balance: String) : GetBalanceSuccess()
    }

    override fun execute(parameters: Unit): Flow<Result<GetBalanceSuccess, GetBalanceErrors>> {
        return flow {
            emit(Result.Loading)
            when (val bitcoinBalanceResult = transactionsRepository.getBalance()) {
                is DataResult.Error -> when (bitcoinBalanceResult.error) {
                    TransactionsRepository.TransactionsRepositoryError.UnknownError ->
                        emit(Result.BusinessRuleError(GetBalanceErrors.NoBalance))
                }

                is DataResult.Success -> emit(
                    Result.Success(
                        GetBalanceSuccess.CurrentBalance(
                            bitcoinBalanceResult.data
                        )
                    )
                )
            }
        }
    }
}
