package com.yurcha.data.repository

import com.yurcha.data.network.safeCall
import com.yurcha.data.room.AppDatabase
import com.yurcha.data.mapper.mapToDomain
import com.yurcha.data.mapper.mapToEntity
import com.yurcha.domain.model.Transaction
import com.yurcha.domain.model.usecase.DataResult
import com.yurcha.domain.repository.TransactionsRepository
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : TransactionsRepository {

    override suspend fun getTransactionsList(offset: Int): DataResult<List<Transaction>, TransactionsRepository.TransactionsRepositoryError> {
        return safeCall(
            call = { database.transactionDao().getTransactions(20, offset).map { it.mapToDomain() } },
            onException = { exception ->
                when (exception) {
                    exception -> TransactionsRepository.TransactionsRepositoryError.UnknownError
                    else -> null
                }
            }
        )
    }

    override suspend fun getBalance(): DataResult<String, TransactionsRepository.TransactionsRepositoryError> {
        return safeCall(
            call = { database.transactionDao().getBalance().toString() },
            onException = { exception ->
                when (exception) {
                    exception -> TransactionsRepository.TransactionsRepositoryError.UnknownError
                    else -> null
                }
            }
        )
    }

    override suspend fun addNewTransaction(transaction: Transaction): DataResult<Unit, TransactionsRepository.TransactionsRepositoryError> {
        return safeCall(
            call = {
                database.transactionDao().insert(transaction.mapToEntity())
            },
            onException = { exception ->
                when (exception) {
                    exception -> TransactionsRepository.TransactionsRepositoryError.UnknownError
                    else -> null
                }
            }
        )
    }
}