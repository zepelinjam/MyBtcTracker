package com.yurcha.domain.repository

import com.yurcha.domain.model.Transaction
import com.yurcha.domain.model.usecase.DataResult
import java.math.BigDecimal

interface TransactionsRepository {

    suspend fun addNewTransaction(transaction: Transaction): DataResult<Unit, TransactionsRepositoryError>

    suspend fun getTransactionsList(offset: Int): DataResult<List<Transaction>, TransactionsRepositoryError>

    suspend fun getBalance(): DataResult<String, TransactionsRepositoryError>

    sealed class TransactionsRepositoryError {
        data object UnknownError : TransactionsRepositoryError()
    }
}
