from fastapi import APIRouter, Depends, Query
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.models.schemas import CohortResponse, ForecastResponse
from app.services.cohort_analysis import get_cohort_retention
from app.services.revenue_forecast import get_revenue_forecast

router = APIRouter(prefix="/analytics", tags=["analytics"])


@router.get("/cohort", response_model=CohortResponse)
async def cohort(
    tenant_id: int = Query(..., description="Tenant internal ID"),
    months: int = Query(6, ge=1, le=12),
    db: AsyncSession = Depends(get_db),
):
    month_labels, cohorts = await get_cohort_retention(db, tenant_id, months)
    return CohortResponse(months=month_labels, cohorts=cohorts)


@router.get("/forecast", response_model=ForecastResponse)
async def forecast(
    tenant_id: int = Query(..., description="Tenant internal ID"),
    history_months: int = Query(6, ge=2, le=24),
    forecast_months: int = Query(3, ge=1, le=6),
    db: AsyncSession = Depends(get_db),
):
    data = await get_revenue_forecast(db, tenant_id, history_months, forecast_months)
    return ForecastResponse(data=data)
