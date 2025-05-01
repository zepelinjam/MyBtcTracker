package com.yurcha.data.repository

import com.yurcha.data.model.toDomain
import com.yurcha.data.network.MviApi
import com.yurcha.data.network.safeApiCall
import com.yurcha.data.room.AppDatabase
import com.yurcha.data.room.dao.BitcoinRateDao
import com.yurcha.data.room.entity.BitcoinRateEntity
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
//    private val bitcoinRateDao: BitcoinRateDao
) : BitcoinRatesRepository {

    override suspend fun getLatestBitcoinRate(): DataResult<BitcoinRate, BitcoinRatesRepository.BitcoinRepositoryError> {
        val latestRateInDb = applicationDatabase.bitcoinRateDao().getLatestRate()

        return if (latestRateInDb == null || isOneHourDifference(latestRateInDb.lastUpdated, Date())) {
            // if no records in DB or updated more than 1 hour ago
            getFromApiAndSaveNewRate()
        } else {
            val rateEntity =  BitcoinRate(
                currency = latestRateInDb.currency,
                currentPrice = latestRateInDb.price,
                lastUpdated = latestRateInDb.lastUpdated
            )
            DataResult.Success(rateEntity)
        }
    }

    private suspend fun getFromApiAndSaveNewRate(): DataResult<BitcoinRate, BitcoinRatesRepository.BitcoinRepositoryError> {
        return safeApiCall(
            apiCall = {
                val bitcoinRateFromApi = mviApi.getBitcoinInfo().toDomain()
                saveLatestBitcoinRateToDB(bitcoinRateFromApi)
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

    private suspend fun saveLatestBitcoinRateToDB(newRate: BitcoinRate) {
        val rateEntity =  BitcoinRateEntity(
            currency = newRate.currency,
            price = newRate.currentPrice,
            lastUpdated = newRate.lastUpdated
        )

        applicationDatabase.bitcoinRateDao().insertNewRate(rateEntity)
    }

    private fun isOneHourDifference(savedDate: Date, currentDate: Date): Boolean {
        val diffInMillis = kotlin.math.abs(savedDate.time - currentDate.time)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        return diffInHours == 1L
    }
}