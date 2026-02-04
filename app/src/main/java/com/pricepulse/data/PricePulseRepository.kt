package com.pricepulse.data

import com.pricepulse.model.PriceHistoryResponse
import com.pricepulse.model.ProductCreateRequest
import com.pricepulse.model.ProductDetailResponse
import com.pricepulse.model.ProductResponse

class PricePulseRepository(
    private val api: PricePulseApi,
) {
    suspend fun loadProducts(): List<ProductResponse> = api.getProducts()

    suspend fun loadProduct(productId: String): ProductDetailResponse = api.getProduct(productId)

    suspend fun loadHistory(productId: String): PriceHistoryResponse = api.getPriceHistory(productId)

    suspend fun addProduct(request: ProductCreateRequest): ProductDetailResponse = api.createProduct(request)
}
