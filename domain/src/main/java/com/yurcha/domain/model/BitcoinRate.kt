package com.yurcha.domain.model

import java.math.BigDecimal
import java.util.Date

/**
 * External data layer representation of a btc rate
 */
data class BitcoinRate(
    val currency: String,
    val currentPrice: String,
    val lastUpdated: Date
) {
    companion object {
        fun fake() = BitcoinRate(
            currency = "USD",
            currentPrice = "94521",
            lastUpdated = Date()
        )
    }
}