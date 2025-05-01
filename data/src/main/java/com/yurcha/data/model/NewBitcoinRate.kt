package com.yurcha.data.model

import com.squareup.moshi.JsonClass
import com.yurcha.domain.model.BitcoinRate
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val maxSupply: Instant,
    val marketCapUsd: String,
    val volumeUsd24Hr: String,
    val priceUsd: String,
    val changePercent24Hr: String,
    val vwap24Hr: String,
    val explorer: String,
)


fun NewBitcoinRate.toDomain() = BitcoinRate(
    currency = data.symbol,
    currentPrice = data.priceUsd,
    lastUpdated = Date()
)