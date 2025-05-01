package com.yurcha.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yurcha.data.room.dao.BitcoinRateDao
import com.yurcha.data.room.dao.TransactionDao
import com.yurcha.data.room.entity.BitcoinRateEntity
import com.yurcha.data.room.entity.TransactionEntity

@Database(entities = [TransactionEntity::class, BitcoinRateEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun bitcoinRateDao(): BitcoinRateDao
}