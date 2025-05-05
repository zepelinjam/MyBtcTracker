package com.yurcha.data.repository

import com.yurcha.data.mapper.mapToDomain
import com.yurcha.data.mapper.mapToEntity
import com.yurcha.data.model.toDomain
import com.yurcha.data.network.MviApi
import com.yurcha.data.network.safeApiCall
import com.yurcha.data.room.AppDatabase
import com.yurcha.domain.model.BitcoinRate
import com.yurcha.domain.model.StatusCode
import com.yurcha.domain.model.usecase.DataResult
import com.yurcha.domain.repository.BitcoinRatesRepository
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BitcoinRatesRepositoryImpl @Inject constructor(
    private val mviApi: MviApi,
    private val applicationDatabase: AppDatabase,
) : BitcoinRatesRepository {

    override suspend fun getLatestBitcoinRate(): DataResult<BitcoinRate, BitcoinRatesRepository.BitcoinRepositoryError> {
        val latestRateInDb = applicationDatabase.bitcoinRateDao().getLatestRate()
        return if (latestRateInDb == null || isOneHourDifference(latestRateInDb.lastUpdated, Date())) {
            // if no records in DB or updated more than 1 hour ago
            getFromApiAndSaveNewRate()
        } else {
            DataResult.Success(latestRateInDb.mapToDomain())
        }
    }

    private suspend fun getFromApiAndSaveNewRate(): DataResult<BitcoinRate, BitcoinRatesRepository.BitcoinRepositoryError> {
        return safeApiCall(
            apiCall = {
                val bitcoinRateFromApi = mviApi.getBitcoinInfo().toDomain()
                applicationDatabase.bitcoinRateDao().insertNewRate(bitcoinRateFromApi.mapToEntity())
                bitcoinRateFromApi
                      },
            onError = { statusCode ->
                when (statusCode) {
                    StatusCode.NoContent -> BitcoinRatesRepository.BitcoinRepositoryError.NoBitcoinRate
                    else -> null
                }
            }
        )
    }

    private fun isOneHourDifference(savedDate: Date, currentDate: Date): Boolean {
        val diffInMillis = kotlin.math.abs(savedDate.time - currentDate.time)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        return diffInHours >= 1L
    }
}