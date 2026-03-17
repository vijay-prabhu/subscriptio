from fastapi import APIRouter, Depends, Query
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.models.schemas import MrrResponse, ChurnResponse
from app.services.mrr_calculator import get_current_mrr, get_mrr_trend
from app.services.churn_calculator import get_current_churn_rate, get_churn_trend

router = APIRouter(prefix="/metrics", tags=["metrics"])


@router.get("/mrr", response_model=MrrResponse)
async def mrr(
    tenant_id: int = Query(..., description="Tenant internal ID"),
    months: int = Query(6, ge=1, le=24),
    db: AsyncSession = Depends(get_db),
):
    current = await get_current_mrr(db, tenant_id)
    trend = await get_mrr_trend(db, tenant_id, months)
    return MrrResponse(current_mrr=current, trend=trend)


@router.get("/churn", response_model=ChurnResponse)
async def churn(
    tenant_id: int = Query(..., description="Tenant internal ID"),
    months: int = Query(6, ge=1, le=24),
    db: AsyncSession = Depends(get_db),
):
    current = await get_current_churn_rate(db, tenant_id)
    trend = await get_churn_trend(db, tenant_id, months)
    return ChurnResponse(current_churn_rate=current, trend=trend)
