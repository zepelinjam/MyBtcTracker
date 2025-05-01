package com.yurcha.domain.repository

import com.yurcha.domain.model.BitcoinRate
import com.yurcha.domain.model.usecase.DataResult

interface BitcoinRatesRepository {

    suspend fun getLatestBitcoinRate(): DataResult<BitcoinRate, BitcoinRepositoryError>
//    suspend fun putLatestBitcoinRate(newRate: BitcoinRate)

    sealed class BitcoinRepositoryError {
        data object NoBitcoinRate : BitcoinRepositoryError()
    }
}
