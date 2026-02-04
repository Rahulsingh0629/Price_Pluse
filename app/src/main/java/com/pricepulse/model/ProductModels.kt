package com.pricepulse.model

data class ProductCreateRequest(
    val name: String,
    val image_url: String?,
    val store_urls: Map<String, String>,
)

data class ProductResponse(
    val id: String,
    val name: String,
    val image_url: String?,
    val created_at: String,
    val store_urls: Map<String, String>?,
    val disclaimer: String?,
)

data class StorePriceResponse(
    val id: Int,
    val product_id: String,
    val store: String,
    val price: Double,
    val product_url: String,
    val fetched_at: String,
)

data class ProductDetailResponse(
    val id: String,
    val name: String,
    val image_url: String?,
    val created_at: String,
    val store_urls: Map<String, String>?,
    val latest_prices: List<StorePriceResponse>,
    val disclaimer: String,
)

data class PriceHistoryResponse(
    val product_id: String,
    val history: List<StorePriceResponse>,
    val disclaimer: String,
)
