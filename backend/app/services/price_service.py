from __future__ import annotations

from datetime import datetime
import json
import logging

from sqlalchemy.orm import Session

from app.models import Product, StorePrice
from app.scrapers.amazon import AmazonScraper
from app.scrapers.base import BaseScraper
from app.scrapers.flipkart import FlipkartScraper

SCRAPER_MAP: dict[str, BaseScraper] = {
    "amazon": AmazonScraper(),
    "flipkart": FlipkartScraper(),
}

logger = logging.getLogger(__name__)


def fetch_prices_for_product(db: Session, product: Product, store_urls: dict[str, str]) -> list[StorePrice]:
    new_prices: list[StorePrice] = []
    for store_key, url in store_urls.items():
        scraper = SCRAPER_MAP.get(store_key)
        if not scraper:
            continue
        try:
            result = scraper.fetch(url)
            record = StorePrice(
                product_id=product.id,
                store=result.store,
                price=result.price,
                product_url=result.product_url,
                fetched_at=datetime.utcnow(),
            )
            db.add(record)
            new_prices.append(record)
        except Exception as exc:
            logger.warning("Failed to scrape %s for %s: %s", store_key, product.id, exc)
            continue

    db.commit()
    for record in new_prices:
        db.refresh(record)
    return new_prices


def parse_store_urls(raw: str) -> dict[str, str]:
    try:
        return json.loads(raw)
    except json.JSONDecodeError:
        return {}


def group_latest_prices(prices: list[StorePrice]) -> list[StorePrice]:
    latest_by_store: dict[str, StorePrice] = {}
    for price in sorted(prices, key=lambda p: p.fetched_at, reverse=True):
        if price.store not in latest_by_store:
            latest_by_store[price.store] = price
    return list(latest_by_store.values())
