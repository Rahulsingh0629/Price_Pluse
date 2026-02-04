import json
from typing import List

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import Product, StorePrice
from app.schemas import ProductCreate, ProductDetailResponse, ProductResponse, PriceHistoryResponse, StorePriceResponse
from app.services.price_service import fetch_prices_for_product, group_latest_prices, parse_store_urls

router = APIRouter(prefix="/products", tags=["products"])

DISCLAIMER = "Prices are scraped from public listings and may change. Use responsibly."


@router.get("", response_model=List[ProductResponse])
def list_products(db: Session = Depends(get_db)):
    products = db.query(Product).all()
    return [
        ProductResponse(
            id=product.id,
            name=product.name,
            image_url=product.image_url,
            created_at=product.created_at,
            store_urls=parse_store_urls(product.store_urls),
            disclaimer=DISCLAIMER,
        )
        for product in products
    ]


@router.post("", response_model=ProductDetailResponse)
def create_product(payload: ProductCreate, db: Session = Depends(get_db)):
    product = Product(
        name=payload.name,
        image_url=payload.image_url,
        store_urls=json.dumps(payload.store_urls),
    )
    db.add(product)
    db.commit()
    db.refresh(product)

    prices = fetch_prices_for_product(db, product, payload.store_urls)
    latest_prices = group_latest_prices(prices)

    return ProductDetailResponse(
        id=product.id,
        name=product.name,
        image_url=product.image_url,
        created_at=product.created_at,
        store_urls=payload.store_urls,
        disclaimer=DISCLAIMER,
        latest_prices=[StorePriceResponse.model_validate(price) for price in latest_prices],
    )


@router.get("/{product_id}", response_model=ProductDetailResponse)
def get_product(product_id: str, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    prices = db.query(StorePrice).filter(StorePrice.product_id == product_id).all()
    latest_prices = group_latest_prices(prices)

    return ProductDetailResponse(
        id=product.id,
        name=product.name,
        image_url=product.image_url,
        created_at=product.created_at,
        store_urls=parse_store_urls(product.store_urls),
        disclaimer=DISCLAIMER,
        latest_prices=[StorePriceResponse.model_validate(price) for price in latest_prices],
    )


@router.get("/{product_id}/prices", response_model=PriceHistoryResponse)
def get_price_history(product_id: str, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    prices = (
        db.query(StorePrice)
        .filter(StorePrice.product_id == product_id)
        .order_by(StorePrice.fetched_at.desc())
        .all()
    )

    return PriceHistoryResponse(
        product_id=product_id,
        history=[StorePriceResponse.model_validate(price) for price in prices],
        disclaimer=DISCLAIMER,
    )
