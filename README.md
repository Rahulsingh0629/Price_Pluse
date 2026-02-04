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

## Free-Tier Friendly Technical Plan
### Option A — Student/Hacker (Scraping)
- **Library:** Jsoup
- **Risk:** Fragile HTML selectors when sites change

### Option B — Professional (Unified APIs)
- **Services:** RapidAPI, PriceAPI, or similar
- **Pros:** Stable JSON
- **Cons:** Free tier limits

## Suggested Tech Stack
- **Language:** Kotlin
- **Networking:** Retrofit + OkHttp
- **Parsing:** Jsoup (scrape) or Moshi/Gson (API)
- **Database:** Room
- **UI:** Jetpack Compose

## Free-Tier Delivery Plan (Advanced)
1. **Start with two sources**
   - Amazon + Flipkart (API if possible, fallback to scraping)
2. **Create a pricing model**
   - Base price + shipping − bank offer = true cost
3. **Local storage for history**
   - Room DB, price snapshots per day
4. **Basic analytics**
   - Show “best price today” and “lowest price in last 30 days”
5. **Deploy minimal backend (optional)**
   - Use a free-tier cloud function for scraping cache to avoid bans

## Suggested Data Model (MVP)
- **Product**: id, title, imageUrl, source, productUrl
- **PriceSnapshot**: productId, price, shipping, offer, trueCost, timestamp
- **WishlistItem**: productId, addedAt, notes

---

If you'd like, I can also draft:
- UI wireframes for the comparison cards
- A Kotlin/Compose project structure
- A scraping/API integration stub

## Run in Android Studio
1. Open the project folder in Android Studio.
2. Let Gradle sync finish.
3. Run the `app` configuration on an emulator or device.

> Note: If Gradle sync fails due to a missing wrapper JAR, run `gradle wrapper`
> from a local Gradle installation or regenerate the wrapper via Android Studio.

## Live data setup
This project is wired for a unified price API. Update the following before release:
1. Update the `BASE_URL` in `PricePulseService` if your backend uses a different host.
2. The UI will fall back to demo data if the API key is not configured.

## Backend (FastAPI)
This repo includes a FastAPI backend that returns product/offer data and can be extended
with real scrapers for each store.

### Run the backend locally
```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

### Notes
- Live scraping is **disabled by default**. Set `ENABLE_LIVE_SCRAPING=true` to enable
  custom scraper logic in `backend/scrapers.py`.
- Real scraping must respect each site's Terms of Service and can break when HTML changes.
