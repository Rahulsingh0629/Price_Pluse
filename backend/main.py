from __future__ import annotations

from fastapi import FastAPI, Query

from scrapers import Product, scrape_all

app = FastAPI(title="PricePulse API")


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


@app.get("/search")
def search(
    query: str = Query(..., min_length=2),
    category: str = Query("All"),
    stores: str = Query("amazon,flipkart,myntra"),
) -> dict:
    products = scrape_all(query, category)
    return {
        "results": [
            {
                "id": product.id,
                "title": product.title,
                "image_url": product.image_url,
                "offers": [
                    {
                        "store": offer.store,
                        "price": offer.price,
                        "shipping": offer.shipping,
                        "offer": offer.offer,
                        "deep_link": offer.deep_link,
                    }
                    for offer in product.offers
                ],
                "price_history": {
                    "current": product.current,
                    "lowest_30d": product.lowest_30d,
                },
            }
            for product in products
        ]
    }
