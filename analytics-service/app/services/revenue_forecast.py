from decimal import Decimal
import numpy as np
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.schemas import ForecastDataPoint


async def get_revenue_forecast(
    db: AsyncSession, tenant_id: int, history_months: int = 6, forecast_months: int = 3
) -> list[ForecastDataPoint]:
    """Simple linear regression forecast on historical MRR data."""
    result = await db.execute(text("""
        SELECT
            to_char(date_trunc('month', i.period_start), 'YYYY-MM') as month,
            COALESCE(SUM(i.total), 0) as mrr
        FROM invoices i
        WHERE i.tenant_id = :tenant_id
          AND i.status = 'PAID'
          AND i.period_start >= now() - make_interval(months => :months)
        GROUP BY date_trunc('month', i.period_start)
        ORDER BY month
    """), {"tenant_id": tenant_id, "months": history_months})

    rows = result.fetchall()
    data_points: list[ForecastDataPoint] = []

    # Add historical data points
    for row in rows:
        data_points.append(ForecastDataPoint(
            month=row[0],
            projected_mrr=Decimal(str(row[1])),
            is_forecast=False,
        ))

    if len(rows) < 2:
        return data_points

    # Linear regression on historical data
    x = np.arange(len(rows), dtype=float)
    y = np.array([float(row[1]) for row in rows])
    coeffs = np.polyfit(x, y, deg=1)

    # Project future months
    from datetime import datetime, timedelta
    last_month = datetime.strptime(rows[-1][0], "%Y-%m")

    for i in range(1, forecast_months + 1):
        future_x = len(rows) - 1 + i
        projected = max(0, float(np.polyval(coeffs, future_x)))
        future_month = last_month.replace(day=1) + timedelta(days=32 * i)
        month_str = future_month.strftime("%Y-%m")

        data_points.append(ForecastDataPoint(
            month=month_str,
            projected_mrr=Decimal(str(round(projected, 2))),
            is_forecast=True,
        ))

    return data_points
