package com.yurcha.data.mapper

import com.yurcha.data.room.entity.TransactionCategory
import com.yurcha.data.room.entity.TransactionEntity
import com.yurcha.data.room.entity.TransactionType
import com.yurcha.domain.model.Transaction

fun Transaction.mapToEntity(): TransactionEntity {
    val category = when (this.category) {
        com.yurcha.domain.model.TransactionCategory.ELECTRONICS -> TransactionCategory.ELECTRONICS
        com.yurcha.domain.model.TransactionCategory.GROCERIES -> TransactionCategory.GROCERIES
        com.yurcha.domain.model.TransactionCategory.TAXI -> TransactionCategory.TAXI
        com.yurcha.domain.model.TransactionCategory.RESTAURANT -> TransactionCategory.RESTAURANT
        com.yurcha.domain.model.TransactionCategory.OTHER -> TransactionCategory.OTHER
    }
    return TransactionEntity(
        date = this.date,
        amount = this.amount.toBigDecimal(),
        type = if (this.isRefill) TransactionType.INCOME else TransactionType.EXPENSE,
        category = category
    )
}

fun TransactionEntity.mapToDomain(): Transaction {
    val category = when (this.category) {
        TransactionCategory.ELECTRONICS -> com.yurcha.domain.model.TransactionCategory.ELECTRONICS
        TransactionCategory.GROCERIES -> com.yurcha.domain.model.TransactionCategory.GROCERIES
        TransactionCategory.TAXI -> com.yurcha.domain.model.TransactionCategory.TAXI
        TransactionCategory.RESTAURANT -> com.yurcha.domain.model.TransactionCategory.RESTAURANT
        TransactionCategory.OTHER ->  com.yurcha.domain.model.TransactionCategory.OTHER
    }
    return Transaction(
        date = this.date,
        amount = this.amount.toString(),
        isRefill = this.type == TransactionType.INCOME,
        category = category
    )
}