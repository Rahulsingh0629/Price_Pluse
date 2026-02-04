package com.pricepulse.data

import com.pricepulse.model.PriceHistoryResponse
import com.pricepulse.model.ProductCreateRequest
import com.pricepulse.model.ProductDetailResponse
import com.pricepulse.model.ProductResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PricePulseApi {
    @GET("products")
    suspend fun getProducts(): List<ProductResponse>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: String): ProductDetailResponse

    @GET("products/{id}/prices")
    suspend fun getPriceHistory(@Path("id") id: String): PriceHistoryResponse

    @POST("products")
    suspend fun createProduct(@Body request: ProductCreateRequest): ProductDetailResponse
}
