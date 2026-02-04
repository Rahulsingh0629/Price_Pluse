package com.pricepulse.model

data class PriceBreakdown(
    val listPrice: String,
    val shipping: String,
    val offer: String,
    val trueCost: String,
)

data class StoreOffer(
    val store: String,
    val price: String,
    val shipping: String,
    val offer: String,
    val deepLink: String,
)

data class ProductResult(
    val id: String,
    val title: String,
    val imageUrl: String,
    val offers: List<StoreOffer>,
    val priceHistory: PriceHistory,
)

data class PriceHistory(
    val current: String,
    val lowest30d: String,
)

data class SearchQuery(
    val query: String,
    val category: String,
)
