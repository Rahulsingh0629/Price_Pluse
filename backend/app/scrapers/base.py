from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass


@dataclass
class ScrapeResult:
    store: str
    title: str
    price: float
    product_url: str


class BaseScraper(ABC):
    store_name: str

    @abstractmethod
    def fetch(self, url: str) -> ScrapeResult:
        raise NotImplementedError
