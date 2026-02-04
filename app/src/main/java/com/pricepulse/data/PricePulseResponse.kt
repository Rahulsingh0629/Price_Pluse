package com.pricepulse.data

import com.squareup.moshi.Json

data class PricePulseResponse(
    @Json(name = "results") val results: List<ApiProduct>,
)

data class ApiProduct(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "offers") val offers: List<ApiOffer>,
    @Json(name = "price_history") val priceHistory: ApiPriceHistory,
)

data class ApiOffer(
    @Json(name = "store") val store: String,
    @Json(name = "price") val price: String,
    @Json(name = "shipping") val shipping: String,
    @Json(name = "offer") val offer: String,
    @Json(name = "deep_link") val deepLink: String,
)

data class ApiPriceHistory(
    @Json(name = "current") val current: String,
    @Json(name = "lowest_30d") val lowest30d: String,
)
