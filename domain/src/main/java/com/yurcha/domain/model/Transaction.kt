package com.yurcha.domain.model

import java.util.Date

/**
 * External data layer representation of a transactions
 */
data class Transaction(
    val date: Date,
    val amount: String,
    val isRefill: Boolean,
    val category: TransactionCategory
) {
    companion object {
        fun fake() = Transaction(
            date = Date(),
            amount = "0.98321",
            isRefill = false,
            category = TransactionCategory.ELECTRONICS
        )
    }
}