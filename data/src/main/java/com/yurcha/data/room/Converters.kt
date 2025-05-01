package com.yurcha.data.room

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.*

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    @JvmStatic
    fun toTimeStamp(date: Date?): Long? = date?.let { date.time }

    @TypeConverter
    @JvmStatic
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toString()

    @TypeConverter
    @JvmStatic
    fun toBigDecimal(value: String?): BigDecimal? = value?.toBigDecimal()

}