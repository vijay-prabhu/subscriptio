from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import settings
from app.api.metrics import router as metrics_router
from app.api.analytics import router as analytics_router
from app.models.schemas import HealthResponse

app = FastAPI(
    title="SubscriptIO Analytics",
    description="Revenue analytics microservice",
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(metrics_router)
app.include_router(analytics_router)


@app.get("/health", response_model=HealthResponse)
async def health():
    return HealthResponse(status="UP", service="subscriptio-analytics")
