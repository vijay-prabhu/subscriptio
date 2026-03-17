from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.schemas import ChurnDataPoint


async def get_current_churn_rate(db: AsyncSession, tenant_id: int) -> float:
    """Churn rate for the current month: cancelled / active at start of month."""
    result = await db.execute(text("""
        WITH month_start AS (
            SELECT date_trunc('month', now()) as dt
        ),
        active_start AS (
            SELECT COUNT(*) as cnt
            FROM subscriptions s, month_start ms
            WHERE s.tenant_id = :tenant_id
              AND s.created_at < ms.dt
              AND (s.cancelled_at IS NULL OR s.cancelled_at >= ms.dt)
              AND s.status IN ('ACTIVE', 'PAST_DUE', 'CANCELLED')
        ),
        churned AS (
            SELECT COUNT(*) as cnt
            FROM subscriptions s, month_start ms
            WHERE s.tenant_id = :tenant_id
              AND s.cancelled_at >= ms.dt
              AND s.cancelled_at < ms.dt + interval '1 month'
        )
        SELECT
            CASE WHEN a.cnt > 0 THEN ROUND(c.cnt::numeric / a.cnt * 100, 2) ELSE 0 END as churn_rate
        FROM active_start a, churned c
    """), {"tenant_id": tenant_id})
    row = result.fetchone()
    return float(row[0]) if row else 0.0


async def get_churn_trend(db: AsyncSession, tenant_id: int, months: int = 6) -> list[ChurnDataPoint]:
    """Monthly churn rate for the last N months."""
    result = await db.execute(text("""
        WITH months AS (
            SELECT generate_series(
                date_trunc('month', now()) - make_interval(months => :months - 1),
                date_trunc('month', now()),
                '1 month'::interval
            ) as month_start
        ),
        monthly_data AS (
            SELECT
                m.month_start,
                (SELECT COUNT(*) FROM subscriptions s
                 WHERE s.tenant_id = :tenant_id
                   AND s.created_at < m.month_start
                   AND (s.cancelled_at IS NULL OR s.cancelled_at >= m.month_start)
                ) as active_start,
                (SELECT COUNT(*) FROM subscriptions s
                 WHERE s.tenant_id = :tenant_id
                   AND s.cancelled_at >= m.month_start
                   AND s.cancelled_at < m.month_start + interval '1 month'
                ) as churned
            FROM months m
        )
        SELECT
            to_char(month_start, 'YYYY-MM') as month,
            CASE WHEN active_start > 0
                 THEN ROUND(churned::numeric / active_start * 100, 2)
                 ELSE 0
            END as churn_rate,
            churned,
            active_start
        FROM monthly_data
        ORDER BY month_start
    """), {"tenant_id": tenant_id, "months": months})

    return [
        ChurnDataPoint(month=row[0], churn_rate=float(row[1]), churned=row[2], active_start=row[3])
        for row in result.fetchall()
    ]
