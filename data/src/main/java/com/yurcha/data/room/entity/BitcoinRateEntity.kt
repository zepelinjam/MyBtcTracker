package com.yurcha.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "bitcoin_rates")
data class BitcoinRateEntity(
    @PrimaryKey val currency: String,
    val price: String,
    val lastUpdated: Date
)