package com.pricepulse.data

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PricePulseApi {
    @GET("search")
    suspend fun searchProducts(
        @Header("X-API-Key") apiKey: String,
        @Query("query") query: String,
        @Query("category") category: String,
        @Query("stores") stores: String,
    ): PricePulseResponse
}
