package com.yurcha.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yurcha.data.room.entity.TransactionEntity
import java.math.BigDecimal

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getTransactions(limit: Int, offset: Int): List<TransactionEntity>

    @Query("SELECT SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END) FROM transactions")
    suspend fun getBalance(): BigDecimal
}