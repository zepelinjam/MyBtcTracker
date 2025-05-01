package com.yurcha.domain.repository

import com.yurcha.domain.model.Transaction
import com.yurcha.domain.model.usecase.DataResult

interface TransactionsRepository {

    suspend fun addNewTransaction(transaction: Transaction): DataResult<List<Transaction>, TransactionsRepositoryError>

    suspend fun getTransactionsList(): DataResult<List<Transaction>, TransactionsRepositoryError>

    sealed class TransactionsRepositoryError {
        data object UnknownError : TransactionsRepositoryError()
    }
}
