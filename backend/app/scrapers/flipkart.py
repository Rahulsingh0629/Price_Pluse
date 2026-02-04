from __future__ import annotations

import re
from typing import Optional

from playwright.sync_api import sync_playwright

from app.scrapers.base import BaseScraper, ScrapeResult
from app.utils.helpers import polite_delay


class FlipkartScraper(BaseScraper):
    store_name = "Flipkart"

    def fetch(self, url: str) -> ScrapeResult:
        polite_delay()
        with sync_playwright() as playwright:
            browser = playwright.chromium.launch(headless=True)
            page = browser.new_page()
            page.goto(url, wait_until="domcontentloaded")
            content = page.content()
            browser.close()

        title = self._extract(content, "span.B_NuCI") or "Flipkart Product"
        price_text = self._extract(content, "div._30jeq3") or "0"
        price = self._parse_price(price_text)

        return ScrapeResult(
            store=self.store_name,
            title=title,
            price=price,
            product_url=url,
        )

    @staticmethod
    def _extract(html: str, selector: str) -> Optional[str]:
        from bs4 import BeautifulSoup

        soup = BeautifulSoup(html, "html.parser")
        node = soup.select_one(selector)
        return node.get_text(strip=True) if node else None

    @staticmethod
    def _parse_price(raw: str) -> float:
        cleaned = re.sub(r"[^0-9.]", "", raw)
        try:
            return float(cleaned)
        except ValueError:
            return 0.0
