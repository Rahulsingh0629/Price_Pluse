# PricePulse — Cross-Platform Price Aggregator (Advanced Free Tier Plan)

PricePulse is a cross-platform price comparison app that searches multiple ecommerce
stores at once and calculates a **True Cost** by adding shipping and applying offers.
This document outlines a free-tier–friendly, advanced plan to build the product.

## Project Identity
- **Name ideas:** PricePulse, DealHunter, CartCompare
- **Type:** Cross-Platform Price Aggregator
- **Core concept:** One search bar → Amazon, Flipkart, Myntra, etc. → **True Cost**

## What Makes It Unique (The Twist)
1. **Hidden Cost Calculator**
   - Example: ₹10,000 + ₹200 shipping − ₹500 bank offer = **₹9,700 true cost**
2. **Price History Graph**
   - Reveal fake discounts: “This item was cheaper 2 weeks ago.”
3. **Unified Wishlist**
   - Save items from any store in a single list.

## Feature Roadmap
### Level 1 — MVP
- **Search Bar:** "Sony Headphones"
- **Data Fetch:** Amazon + Flipkart via scraping or APIs
- **Comparison Card:** Image, title, price A vs. price B
- **Deep Links:** Open directly in store apps

### Level 2 — Logic Upgrade
- **Price History:** Store prices in local DB (Room)
- **Price Drop Alerts:** Detect and notify when cheaper
- **Filters:** Cheapest, fastest delivery

### Level 3 — AI Feature
- **Visual Search:** Take a photo in-store → find online
- **Tech:** ML Kit image labeling / similarity matching

## Suggested Tech Stack
- **Language:** Kotlin
- **Networking:** Retrofit + OkHttp
- **Parsing:** BeautifulSoup + Playwright (scraping)
- **Database:** SQLite (local) + SQLAlchemy (backend)
- **UI:** Jetpack Compose

---

## Run in Android Studio
1. Open the project folder in Android Studio.
2. Let Gradle sync finish.
3. Run the `app` configuration on an emulator or device.

> Note: If Gradle sync fails due to a missing wrapper JAR, run `gradle wrapper`
> from a local Gradle installation or regenerate the wrapper via Android Studio.

## Backend (FastAPI)
This repo includes a FastAPI backend that scrapes prices from store URLs, stores
price history, and exposes clean JSON endpoints for the mobile app.

### Setup
```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python -m playwright install
python run.py
```

### Scraping disclaimer
Prices are scraped from public listings and may change at any time. Use responsibly
and respect the Terms of Service of each store.

### API endpoints
- `GET /products`
- `GET /products/{id}`
- `GET /products/{id}/prices`
- `POST /products` (include store URLs)

### Daily price tracking
The backend runs a daily job (APScheduler) that fetches prices for all tracked products
and appends new price history records.
