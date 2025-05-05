package com.yurcha.domain.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import java.util.logging.Level
import java.util.logging.Logger

private const val DATE_FORMAT = "dd.MM.yyyy"
private const val TIME_FORMAT = "HH:mm:ss"
private const val DATE_TIME_FORMAT = "$DATE_FORMAT $TIME_FORMAT"

// function for to scale the BTC value to 2 points after the dot
fun String.defScale(): String {
    return try {
        String.format("%.2f", this.toDouble())
    } catch (e: NumberFormatException) {
        this
    }
}

/**
 * Pattern: dd.MM.yyyy
 */
fun Date.toDateFormat(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    return sdf.format(this)
}

fun Date.toDateTimeFormat(): String {
    val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
    return sdf.format(this)
}

fun Long.toDateFormat(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: HH:mm:ss
 */
fun Date.toTimeFormat(): String {
    val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: HH:mm
 */

fun Long.toTimeFormat(): String {
    val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: dd.MM.yyyy HH:mm:ss
 */
fun Date.toPrintFormat(): String {
    val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
    return sdf.format(this)
}

/**
 * Add field date to current date
 */
fun Date.add(field: Int, amount: Int): Date {
    val cal = getInstance()
    cal.time = this
    cal.add(field, amount)
    this.time = cal.timeInMillis
    cal.clear()
    return Date(this.time)
}

fun Date.get(field: Int): Int {
    val cal = getInstance()
    cal.time = this
    return cal.get(field)
}

fun Date.addYears(years: Int): Date {
    return add(YEAR, years)
}

fun Date.addMonths(months: Int): Date {
    return add(MONTH, months)
}

fun Date.addDays(days: Int): Date {
    return add(DAY_OF_MONTH, days)
}

fun Date.getYears(): Int {
    return get(YEAR)
}

fun Date.getDateHours(): Int {
    val cal = getInstance()
    cal.time = this
    return cal.get(HOUR_OF_DAY)
}

fun Date.getDateMinutes(): Int {
    val cal = getInstance()
    cal.time = this
    return cal.get(MINUTE)
}

fun Date.getDateSeconds(): Int {
    val cal = getInstance()
    cal.time = this
    return cal.get(SECOND)
}

fun Date.addDaysForWeek(days: Int): Date {
    return add(DAY_OF_WEEK, days)
}


fun Long.removeTimeInDate(): Long {
    val c = getInstance()
    c.timeInMillis = this
    c.set(HOUR_OF_DAY, 0)
    c.set(MINUTE, 0)
    c.set(SECOND, 0)
    c.set(MILLISECOND, 0)
    return c.timeInMillis
}

fun Date.removeTimeInDate(): Date {
    val c = getInstance()
    c.timeInMillis = this.time
    c.set(HOUR_OF_DAY, 0)
    c.set(MINUTE, 0)
    c.set(SECOND, 0)
    c.set(MILLISECOND, 0)
    return Date(c.timeInMillis)
}

fun Date.removeMillisInDate(): Date {
    val c = getInstance()
    c.timeInMillis = this.time
    c.set(MILLISECOND, 0)
    return Date(c.timeInMillis)
}

fun Date.removeOneDay(): Date {
    return add(DAY_OF_MONTH, -1)
}

fun Date.removeDays(amountDays: Int): Date {
    return add(DAY_OF_MONTH, -amountDays)
}

fun Date.setTime(hour: Int, minutes: Int, seconds: Int): Date {
    val c = getInstance()
    c.timeInMillis = this.time
    c.set(HOUR_OF_DAY, hour)
    c.set(MINUTE, minutes)
    c.set(SECOND, seconds)
    c.set(MILLISECOND, 0)
    return Date(c.timeInMillis)
}

fun Long.getDayNumberFromDate(): Int {
    val c = getInstance()
    c.timeInMillis = this
    return c.get(DAY_OF_MONTH)
}

fun String.parseTime(): Date? {
    return try {
        val simpleDateFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        var index = indexOf(".")
        if (index == -1) index = lastIndex
        simpleDateFormat.parse(substring(0, index))
    } catch (e: Exception) {
        Logger.getGlobal().log(Level.INFO, "ParseTime error", e)
        null
    }
}

/**
 * Pattern: dd.MM.yyyy HH:mm:ss
 */
fun Long.toDateTimeFormat(): String {
    val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
    return sdf.format(this)
}