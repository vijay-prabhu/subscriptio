from typing import List, Optional

from pydantic import BaseModel
from decimal import Decimal


class MrrDataPoint(BaseModel):
    month: str
    mrr: Decimal


class MrrResponse(BaseModel):
    current_mrr: Decimal
    trend: list[MrrDataPoint]


class ChurnDataPoint(BaseModel):
    month: str
    churn_rate: float
    churned: int
    active_start: int


class ChurnResponse(BaseModel):
    current_churn_rate: float
    trend: list[ChurnDataPoint]


class CohortRow(BaseModel):
    cohort: str
    size: int
    retention: List[Optional[float]]


class CohortResponse(BaseModel):
    months: list[str]
    cohorts: list[CohortRow]


class ForecastDataPoint(BaseModel):
    month: str
    projected_mrr: Decimal
    is_forecast: bool


class ForecastResponse(BaseModel):
    data: list[ForecastDataPoint]


class HealthResponse(BaseModel):
    status: str
    service: str
