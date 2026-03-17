from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.schemas import CohortRow


async def get_cohort_retention(db: AsyncSession, tenant_id: int, months: int = 6) -> tuple[list[str], list[CohortRow]]:
    """Monthly cohort retention analysis.

    Groups customers by signup month. For each subsequent month,
    calculates what percentage still have an active subscription.
    """
    result = await db.execute(text("""
        WITH cohorts AS (
            SELECT
                c.id as customer_id,
                date_trunc('month', c.created_at) as cohort_month
            FROM customers c
            WHERE c.tenant_id = :tenant_id
              AND c.created_at >= now() - make_interval(months => :months)
        ),
        months AS (
            SELECT generate_series(0, :months - 1) as month_offset
        ),
        retention AS (
            SELECT
                co.cohort_month,
                m.month_offset,
                COUNT(DISTINCT co.customer_id) FILTER (
                    WHERE EXISTS (
                        SELECT 1 FROM subscriptions s
                        WHERE s.customer_id = co.customer_id
                          AND s.tenant_id = :tenant_id
                          AND s.created_at <= co.cohort_month + make_interval(months => m.month_offset + 1)
                          AND (s.cancelled_at IS NULL
                               OR s.cancelled_at > co.cohort_month + make_interval(months => m.month_offset))
                    )
                ) as retained,
                COUNT(DISTINCT co.customer_id) as cohort_size
            FROM cohorts co
            CROSS JOIN months m
            WHERE co.cohort_month + make_interval(months => m.month_offset) <= date_trunc('month', now())
            GROUP BY co.cohort_month, m.month_offset
        )
        SELECT
            to_char(cohort_month, 'YYYY-MM') as cohort,
            cohort_size,
            month_offset,
            CASE WHEN cohort_size > 0
                 THEN ROUND(retained::numeric / cohort_size * 100, 1)
                 ELSE NULL
            END as retention_pct
        FROM retention
        ORDER BY cohort_month, month_offset
    """), {"tenant_id": tenant_id, "months": months})

    rows = result.fetchall()

    # Build cohort data structure
    cohort_map: dict[str, CohortRow] = {}
    month_labels: list[str] = [f"Month {i}" for i in range(months)]

    for row in rows:
        cohort_label = row[0]
        cohort_size = row[1]
        offset = row[2]
        retention_pct = float(row[3]) if row[3] is not None else None

        if cohort_label not in cohort_map:
            cohort_map[cohort_label] = CohortRow(
                cohort=cohort_label,
                size=cohort_size,
                retention=[None] * months,
            )
        if offset < months:
            cohort_map[cohort_label].retention[offset] = retention_pct

    return month_labels, list(cohort_map.values())
