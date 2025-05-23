package com.yurcha.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: Date,
    val amount: BigDecimal,
    val type: TransactionType,
    val category: TransactionCategory
)

enum class TransactionType { INCOME, EXPENSE }

enum class TransactionCategory { GROCERIES, TAXI, ELECTRONICS, RESTAURANT, OTHER }