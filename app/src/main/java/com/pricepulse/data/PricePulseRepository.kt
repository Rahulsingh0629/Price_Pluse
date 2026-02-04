package com.pricepulse.data

import com.pricepulse.BuildConfig
import com.pricepulse.model.PriceHistory
import com.pricepulse.model.ProductResult
import com.pricepulse.model.StoreOffer

class PricePulseRepository(
    private val api: PricePulseApi,
) {
    suspend fun search(query: String, category: String): List<ProductResult> {
        if (BuildConfig.PRICEPULSE_API_KEY == "YOUR_API_KEY") {
            return demoResults()
        }
        val response = api.searchProducts(
            apiKey = BuildConfig.PRICEPULSE_API_KEY,
            query = query,
            category = category,
            stores = "amazon,flipkart,myntra",
        )
        return response.results.map { product ->
            ProductResult(
                id = product.id,
                title = product.title,
                imageUrl = product.imageUrl,
                offers = product.offers.map { offer ->
                    StoreOffer(
                        store = offer.store,
                        price = offer.price,
                        shipping = offer.shipping,
                        offer = offer.offer,
                        deepLink = offer.deepLink,
                    )
                },
                priceHistory = PriceHistory(
                    current = product.priceHistory.current,
                    lowest30d = product.priceHistory.lowest30d,
                ),
            )
        }
    }

    private fun demoResults(): List<ProductResult> = listOf(
        ProductResult(
            id = "sony-xm5",
            title = "Sony WH-1000XM5 Wireless Noise Cancelling Headphones",
            imageUrl = "https://example.com/images/sony-xm5.png",
            offers = listOf(
                StoreOffer(
                    store = "Amazon",
                    price = "₹24,990",
                    shipping = "₹0",
                    offer = "-₹2,000",
                    deepLink = "https://www.amazon.in/",
                ),
                StoreOffer(
                    store = "Flipkart",
                    price = "₹25,499",
                    shipping = "₹120",
                    offer = "-₹1,500",
                    deepLink = "https://www.flipkart.com/",
                ),
            ),
            priceHistory = PriceHistory(
                current = "₹23,990",
                lowest30d = "₹22,500",
            ),
        ),
        ProductResult(
            id = "nike-pegasus-40",
            title = "Nike Pegasus 40 Running Shoes",
            imageUrl = "https://example.com/images/nike-pegasus-40.png",
            offers = listOf(
                StoreOffer(
                    store = "Amazon",
                    price = "₹8,999",
                    shipping = "₹0",
                    offer = "-₹500",
                    deepLink = "https://www.amazon.in/",
                ),
                StoreOffer(
                    store = "Myntra",
                    price = "₹9,250",
                    shipping = "₹80",
                    offer = "-₹750",
                    deepLink = "https://www.myntra.com/",
                ),
            ),
            priceHistory = PriceHistory(
                current = "₹8,499",
                lowest30d = "₹7,999",
            ),
        ),
    )
}
