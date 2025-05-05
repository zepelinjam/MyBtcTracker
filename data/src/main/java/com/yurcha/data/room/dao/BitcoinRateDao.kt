package com.yurcha.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yurcha.data.room.entity.BitcoinRateEntity

@Dao
interface BitcoinRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewRate(rateEntity: BitcoinRateEntity)

    @Query("SELECT * FROM bitcoin_rates WHERE currency = :currency LIMIT 1")
    suspend fun getLatestRate(currency: String = "BTC"): BitcoinRateEntity?
}