package com.yurcha.data.network

import com.yurcha.data.model.NewBitcoinRate
import retrofit2.http.GET

interface MviApi {
    @GET("bitcoin")
    suspend fun getBitcoinInfo(): NewBitcoinRate
}
