from fastapi import FastAPI

from app.database import Base, engine
from app.routes.product_routes import router as product_router
from app.scheduler.daily_job import start_scheduler

app = FastAPI(title="PricePulse API")
app.include_router(product_router)

scheduler = None


@app.on_event("startup")
def startup_event() -> None:
    Base.metadata.create_all(bind=engine)
    global scheduler
    scheduler = start_scheduler()


@app.on_event("shutdown")
def shutdown_event() -> None:
    if scheduler:
        scheduler.shutdown()


def run() -> None:
    import uvicorn

    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
