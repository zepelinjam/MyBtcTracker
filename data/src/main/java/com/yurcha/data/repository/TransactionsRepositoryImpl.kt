package com.yurcha.data.repository

import com.yurcha.data.network.safeCall
import com.yurcha.data.room.AppDatabase
import com.yurcha.data.room.entity.TransactionCategory
import com.yurcha.data.room.entity.TransactionEntity
import com.yurcha.data.room.entity.TransactionType
import com.yurcha.domain.model.Transaction
import com.yurcha.domain.model.usecase.DataResult
import com.yurcha.domain.repository.TransactionsRepository
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : TransactionsRepository {

    override suspend fun getTransactionsList(): DataResult<List<Transaction>, TransactionsRepository.TransactionsRepositoryError> {
        return safeCall(
            call = { database.transactionDao().getTransactions(20, 20).map { it.mapToDomain() } },
            onException = { exception ->
                when (exception) {
                    exception -> TransactionsRepository.TransactionsRepositoryError.UnknownError
                    else -> null
                }
            }
        )
    }

    override suspend fun addNewTransaction(transaction: Transaction): DataResult<List<Transaction>, TransactionsRepository.TransactionsRepositoryError> {
        database.transactionDao().insert(transaction.mapToEntity())
        return getTransactionsList()
    }

    private fun Transaction.mapToEntity(): TransactionEntity {
        val category = when (this.category) {
            com.yurcha.domain.model.TransactionCategory.ELECTRONICS -> TransactionCategory.ELECTRONICS
            com.yurcha.domain.model.TransactionCategory.GROCERIES -> TransactionCategory.GROCERIES
            com.yurcha.domain.model.TransactionCategory.TAXI -> TransactionCategory.TAXI
            com.yurcha.domain.model.TransactionCategory.RESTAURANT -> TransactionCategory.RESTAURANT
            com.yurcha.domain.model.TransactionCategory.OTHER -> TransactionCategory.OTHER
        }

        return TransactionEntity(
            date = this.date,
            amount = this.amount,
            type = if (this.isRefill) TransactionType.INCOME else TransactionType.EXPENSE,
            category = category
        )
    }

    private fun TransactionEntity.mapToDomain(): Transaction {
        val category = when (this.category) {
            TransactionCategory.ELECTRONICS -> com.yurcha.domain.model.TransactionCategory.ELECTRONICS
            TransactionCategory.GROCERIES -> com.yurcha.domain.model.TransactionCategory.GROCERIES
            TransactionCategory.TAXI -> com.yurcha.domain.model.TransactionCategory.TAXI
            TransactionCategory.RESTAURANT -> com.yurcha.domain.model.TransactionCategory.RESTAURANT
            TransactionCategory.OTHER ->  com.yurcha.domain.model.TransactionCategory.OTHER
        }

        return Transaction(
            date = this.date,
            amount = this.amount,
            isRefill = this.type == TransactionType.INCOME,
            category = category
        )
    }
}