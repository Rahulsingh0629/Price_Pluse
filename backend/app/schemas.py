from datetime import datetime

from pydantic import BaseModel, Field


class ProductCreate(BaseModel):
    name: str = Field(..., min_length=2)
    image_url: str | None = None
    store_urls: dict[str, str]


class ProductResponse(BaseModel):
    id: str
    name: str
    image_url: str | None
    created_at: datetime
    store_urls: dict[str, str] | None = None
    disclaimer: str | None = None

    class Config:
        from_attributes = True


class StorePriceResponse(BaseModel):
    id: int
    product_id: str
    store: str
    price: float
    product_url: str
    fetched_at: datetime

    class Config:
        from_attributes = True


class ProductDetailResponse(ProductResponse):
    latest_prices: list[StorePriceResponse]


class PriceHistoryResponse(BaseModel):
    product_id: str
    history: list[StorePriceResponse]
    disclaimer: str
