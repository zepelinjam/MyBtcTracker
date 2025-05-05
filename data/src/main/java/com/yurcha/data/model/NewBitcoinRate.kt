package com.yurcha.data.model

import com.squareup.moshi.JsonClass
import com.yurcha.domain.model.BitcoinRate
import com.yurcha.domain.utils.defScale
import com.yurcha.domain.utils.toDateFormat
import com.yurcha.domain.utils.toDateTimeFormat
import java.util.Date

@JsonClass(generateAdapter = true)
data class NewBitcoinRate(
    val data: Data,
    val timestamp: String
)

@JsonClass(generateAdapter = true)
data class Data(
    val id: String,
    val rank: String,
    val symbol: String,
    val name: String,
    val supply: String,
    val maxSupply: String,
    val marketCapUsd: String,
    val volumeUsd24Hr: String,
    val priceUsd: String,
    val changePercent24Hr: String,
    val vwap24Hr: String,
    val explorer: String,
)

fun NewBitcoinRate.toDomain() = BitcoinRate(
    currency = data.symbol,
    currentPrice = data.priceUsd.defScale(),
    lastUpdated = Date().toDateTimeFormat()
)