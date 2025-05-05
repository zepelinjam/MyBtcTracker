package com.yurcha.domain.model

/**
 * External data layer representation of a btc rate
 */
data class BitcoinRate(
    val currency: String,
    val currentPrice: String,
    val lastUpdated: String
) {
    companion object {
        fun fake() = BitcoinRate(
            currency = "USD",
            currentPrice = "94521",
            lastUpdated = "10.12.2024 14:42:21"
        )
    }
}