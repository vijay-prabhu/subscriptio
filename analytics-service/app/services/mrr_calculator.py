from decimal import Decimal
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.schemas import MrrDataPoint


async def get_current_mrr(db: AsyncSession, tenant_id: int) -> Decimal:
    """Sum of monthly-normalized prices for all active subscriptions."""
    result = await db.execute(text("""
        SELECT COALESCE(SUM(
            CASE WHEN p.billing_interval = 'yearly'
                 THEN p.price / 12
                 ELSE p.price
            END
        ), 0) as mrr
        FROM subscriptions s
        JOIN plans p ON s.plan_id = p.id
        WHERE s.tenant_id = :tenant_id AND s.status = 'ACTIVE'
    """), {"tenant_id": tenant_id})
    row = result.fetchone()
    return Decimal(str(row[0])) if row else Decimal("0")


async def get_mrr_trend(db: AsyncSession, tenant_id: int, months: int = 6) -> list[MrrDataPoint]:
    """MRR by month for the last N months based on invoice data."""
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
    """), {"tenant_id": tenant_id, "months": months})

    return [MrrDataPoint(month=row[0], mrr=Decimal(str(row[1]))) for row in result.fetchall()]
