package com.yurcha.data.network

import com.yurcha.data.model.NewBitcoinRate
import retrofit2.http.GET
import retrofit2.http.Path

interface MviApi {
//    @GET("topics")
//    suspend fun getTopics(): List<NetworkTopic>

    @GET("bitcoin")
    suspend fun getBitcoinInfo(): NewBitcoinRate
}
