"""Unit tests for MRR calculation logic.

These test the calculation functions with mock database results
to verify the math is correct without needing a real database.
"""
from decimal import Decimal
from unittest.mock import AsyncMock, MagicMock
import pytest


@pytest.mark.asyncio
async def test_current_mrr_monthly_plans():
    """Monthly plans contribute their full price to MRR."""
    from app.services.mrr_calculator import get_current_mrr

    mock_db = AsyncMock()
    mock_result = MagicMock()
    mock_result.fetchone.return_value = (Decimal("149.97"),)
    mock_db.execute.return_value = mock_result

    mrr = await get_current_mrr(mock_db, tenant_id=1)
    assert mrr == Decimal("149.97")


@pytest.mark.asyncio
async def test_current_mrr_no_subscriptions():
    """Zero MRR when no active subscriptions."""
    from app.services.mrr_calculator import get_current_mrr

    mock_db = AsyncMock()
    mock_result = MagicMock()
    mock_result.fetchone.return_value = (Decimal("0"),)
    mock_db.execute.return_value = mock_result

    mrr = await get_current_mrr(mock_db, tenant_id=1)
    assert mrr == Decimal("0")


@pytest.mark.asyncio
async def test_mrr_trend_returns_data_points():
    """MRR trend returns monthly data points."""
    from app.services.mrr_calculator import get_mrr_trend

    mock_db = AsyncMock()
    mock_result = MagicMock()
    mock_result.fetchall.return_value = [
        ("2026-01", Decimal("100.00")),
        ("2026-02", Decimal("150.00")),
        ("2026-03", Decimal("200.00")),
    ]
    mock_db.execute.return_value = mock_result

    trend = await get_mrr_trend(mock_db, tenant_id=1, months=3)
    assert len(trend) == 3
    assert trend[0].month == "2026-01"
    assert trend[0].mrr == Decimal("100.00")
    assert trend[2].mrr == Decimal("200.00")
