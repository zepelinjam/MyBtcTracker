package com.yurcha.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yurcha.data.room.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getTransactions(limit: Int, offset: Int): List<TransactionEntity>
}