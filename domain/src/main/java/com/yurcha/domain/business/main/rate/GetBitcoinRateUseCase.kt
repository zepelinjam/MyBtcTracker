package com.yurcha.domain.business.main.rate

import com.yurcha.domain.business.base.FlowUseCase
import com.yurcha.domain.di.IoDispatcher
import com.yurcha.domain.model.BitcoinRate
import com.yurcha.domain.model.usecase.DataResult
import com.yurcha.domain.model.usecase.Result
import com.yurcha.domain.repository.BitcoinRatesRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetBitcoinRateUseCase @Inject constructor(
    private val ratesRepository: BitcoinRatesRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, GetBitcoinRateUseCase.GetBitcoinRateSuccess, GetBitcoinRateUseCase.GetBitcoinRateErrors>(dispatcher) {

    sealed class GetBitcoinRateErrors {
        data object NoRate : GetBitcoinRateErrors()
    }

    sealed class GetBitcoinRateSuccess {
        data class BitcoinRateData(val bitcoinRates: BitcoinRate) : GetBitcoinRateSuccess()
    }

    override fun execute(parameters: Unit): Flow<Result<GetBitcoinRateSuccess, GetBitcoinRateErrors>> {
        return flow {
            emit(Result.Loading)

            when (val bitcoinRateResult = ratesRepository.getLatestBitcoinRate()) {
                is DataResult.Error -> when (bitcoinRateResult.error) {
                    BitcoinRatesRepository.BitcoinRepositoryError.NoBitcoinRate ->
                        emit(Result.BusinessRuleError(GetBitcoinRateErrors.NoRate))
                }

                is DataResult.Success -> emit(
                    Result.Success(
                        GetBitcoinRateSuccess.BitcoinRateData(
                            bitcoinRateResult.data
                        )
                    )
                )
            }
        }
    }
}
