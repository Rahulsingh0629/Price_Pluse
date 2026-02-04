from __future__ import annotations

import logging

from apscheduler.schedulers.background import BackgroundScheduler
from sqlalchemy.orm import Session

from app.database import SessionLocal
from app.models import Product
from app.services.price_service import fetch_prices_for_product, parse_store_urls

logger = logging.getLogger(__name__)


def run_daily_price_check() -> None:
    db: Session = SessionLocal()
    try:
        products = db.query(Product).all()
        for product in products:
            store_urls = parse_store_urls(product.store_urls)
            if not store_urls:
                continue
            fetch_prices_for_product(db, product, store_urls)
    finally:
        db.close()


def start_scheduler() -> BackgroundScheduler:
    scheduler = BackgroundScheduler()
    scheduler.add_job(run_daily_price_check, "interval", days=1)
    scheduler.start()
    return scheduler
