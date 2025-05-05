package com.yurcha.domain.business.main

import com.yurcha.domain.business.base.FlowUseCase
import com.yurcha.domain.di.IoDispatcher
import com.yurcha.domain.model.BitcoinRate
import com.yurcha.domain.model.Transaction
import com.yurcha.domain.model.usecase.DataResult
import com.yurcha.domain.model.usecase.Result
import com.yurcha.domain.repository.BitcoinRatesRepository
import com.yurcha.domain.repository.TransactionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainDataUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val bitcoinRatesRepository: BitcoinRatesRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Int, MainDataUseCase.MainDataSuccess, MainDataUseCase.MainDataErrors>(dispatcher) {

    sealed class MainDataErrors {
        data object NoTransactionsFound : MainDataErrors()
        data object NoBitcoinRateFound : MainDataErrors()
    }

    sealed class MainDataSuccess {
        data class TransactionsData(val transactions: List<Transaction>) : MainDataSuccess()
        data class BitcoinRateData(val bitcoinRates: BitcoinRate) : MainDataSuccess()
        data class CurrentBalance(val balance: String) : MainDataSuccess()
    }

    override fun execute(parameters: Int): Flow<Result<MainDataSuccess, MainDataErrors>> {
        return flow {
            emit(Result.Loading)

            val transactionsResult = transactionsRepository.getTransactionsList(offset = parameters)
            val bitcoinRatesResult = bitcoinRatesRepository.getLatestBitcoinRate()
            val bitcoinBalanceResult = transactionsRepository.getBalance()

            when (transactionsResult) {
                is DataResult.Error -> when (transactionsResult.error) {
                    TransactionsRepository.TransactionsRepositoryError.UnknownError ->
                        emit(Result.BusinessRuleError(MainDataErrors.NoTransactionsFound))
                }

                is DataResult.Success -> emit(
                    Result.Success(MainDataSuccess.TransactionsData(transactionsResult.data))
                )
            }

            when (bitcoinBalanceResult) {
                is DataResult.Error -> when (bitcoinBalanceResult.error) {
                    TransactionsRepository.TransactionsRepositoryError.UnknownError ->
                        emit(Result.BusinessRuleError(MainDataErrors.NoTransactionsFound))
                }

                is DataResult.Success -> emit(
                    Result.Success(MainDataSuccess.CurrentBalance(bitcoinBalanceResult.data))
                )
            }

            when (bitcoinRatesResult) {
                is DataResult.Error -> when (bitcoinRatesResult.error) {
                    BitcoinRatesRepository.BitcoinRepositoryError.NoBitcoinRate ->
                        emit(Result.BusinessRuleError(MainDataErrors.NoBitcoinRateFound))
                }

                is DataResult.Success -> emit(
                    Result.Success(MainDataSuccess.BitcoinRateData(bitcoinRatesResult.data))
                )
            }
        }
    }
}
