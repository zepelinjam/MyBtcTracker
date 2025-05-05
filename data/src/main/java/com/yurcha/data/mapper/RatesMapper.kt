package com.yurcha.data.mapper

import com.yurcha.data.room.entity.BitcoinRateEntity
import com.yurcha.domain.model.BitcoinRate
import com.yurcha.domain.utils.defScale
import com.yurcha.domain.utils.toDateTimeFormat
import java.util.Date

fun BitcoinRateEntity.mapToDomain(): BitcoinRate {
    return BitcoinRate(
        currency = this.currency,
        currentPrice = this.price.defScale(),
        lastUpdated = this.lastUpdated.toDateTimeFormat()
    )
}

fun BitcoinRate.mapToEntity(): BitcoinRateEntity {
    return BitcoinRateEntity(
        currency = this.currency,
        price = this.currentPrice.defScale(),
        lastUpdated = Date()
    )
}