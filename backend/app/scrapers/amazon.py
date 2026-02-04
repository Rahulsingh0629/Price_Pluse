from __future__ import annotations

import re
from typing import Optional

import requests
from bs4 import BeautifulSoup

from app.config import settings
from app.scrapers.base import BaseScraper, ScrapeResult
from app.utils.helpers import polite_delay, random_user_agent


class AmazonScraper(BaseScraper):
    store_name = "Amazon"

    def fetch(self, url: str) -> ScrapeResult:
        headers = {"User-Agent": random_user_agent()}
        polite_delay()
        response = requests.get(url, headers=headers, timeout=settings.request_timeout)
        response.raise_for_status()
        soup = BeautifulSoup(response.text, "html.parser")

        title = self._text(soup.select_one("#productTitle")) or "Amazon Product"
        price_text = (
            self._text(soup.select_one("#priceblock_ourprice"))
            or self._text(soup.select_one("#priceblock_dealprice"))
            or self._text(soup.select_one(".a-price .a-offscreen"))
            or "0"
        )
        price = self._parse_price(price_text)

        return ScrapeResult(
            store=self.store_name,
            title=title,
            price=price,
            product_url=url,
        )

    @staticmethod
    def _text(node) -> Optional[str]:
        return node.get_text(strip=True) if node else None

    @staticmethod
    def _parse_price(raw: str) -> float:
        cleaned = re.sub(r"[^0-9.]", "", raw)
        try:
            return float(cleaned)
        except ValueError:
            return 0.0
