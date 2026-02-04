from __future__ import annotations

import os
from dataclasses import dataclass
from typing import Iterable, List

import httpx
from bs4 import BeautifulSoup


@dataclass
class Offer:
    store: str
    price: str
    shipping: str
    offer: str
    deep_link: str


@dataclass
class Product:
    id: str
    title: str
    image_url: str
    offers: List[Offer]
    current: str
    lowest_30d: str


def scrape_all(query: str, category: str) -> List[Product]:
    if os.getenv("ENABLE_LIVE_SCRAPING") != "true":
        return demo_results(query)

    # NOTE: Real scraping requires careful compliance with each site's Terms of Service
    # and stable selectors. This function is intentionally minimal and should be
    # extended responsibly.
    stores = ["Amazon", "Flipkart", "Myntra"]
    products: List[Product] = []

    for store in stores:
        # Placeholder scraping call; replace with site-specific scrapers.
        offers = [
            Offer(
                store=store,
                price="₹0",
                shipping="₹0",
                offer="₹0",
                deep_link="https://www.example.com",
            )
        ]
        products.append(
            Product(
                id=f"{store.lower()}-{query[:6]}",
                title=f"{query} ({store})",
                image_url="https://via.placeholder.com/300",
                offers=offers,
                current="₹0",
                lowest_30d="₹0",
            )
        )

    return products


def demo_results(query: str) -> List[Product]:
    return [
        Product(
            id="sony-xm5",
            title=f"{query} — Sony WH-1000XM5",
            image_url="https://example.com/images/sony-xm5.png",
            offers=[
                Offer(
                    store="Amazon",
                    price="₹24,990",
                    shipping="₹0",
                    offer="-₹2,000",
                    deep_link="https://www.amazon.in/",
                ),
                Offer(
                    store="Flipkart",
                    price="₹25,499",
                    shipping="₹120",
                    offer="-₹1,500",
                    deep_link="https://www.flipkart.com/",
                ),
            ],
            current="₹23,990",
            lowest_30d="₹22,500",
        ),
        Product(
            id="nike-pegasus-40",
            title="Nike Pegasus 40 Running Shoes",
            image_url="https://example.com/images/nike-pegasus-40.png",
            offers=[
                Offer(
                    store="Amazon",
                    price="₹8,999",
                    shipping="₹0",
                    offer="-₹500",
                    deep_link="https://www.amazon.in/",
                ),
                Offer(
                    store="Myntra",
                    price="₹9,250",
                    shipping="₹80",
                    offer="-₹750",
                    deep_link="https://www.myntra.com/",
                ),
            ],
            current="₹8,499",
            lowest_30d="₹7,999",
        ),
    ]
