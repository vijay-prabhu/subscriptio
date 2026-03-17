"""Unit tests for churn rate calculation."""
from unittest.mock import AsyncMock, MagicMock
import pytest


@pytest.mark.asyncio
async def test_current_churn_rate():
    """Churn rate is churned / active_start * 100."""
    from app.services.churn_calculator import get_current_churn_rate

    mock_db = AsyncMock()
    mock_result = MagicMock()
    mock_result.fetchone.return_value = (5.0,)
    mock_db.execute.return_value = mock_result

    rate = await get_current_churn_rate(mock_db, tenant_id=1)
    assert rate == 5.0


@pytest.mark.asyncio
async def test_churn_rate_zero_when_no_data():
    """Zero churn when no active subscriptions."""
    from app.services.churn_calculator import get_current_churn_rate

    mock_db = AsyncMock()
    mock_result = MagicMock()
    mock_result.fetchone.return_value = (0.0,)
    mock_db.execute.return_value = mock_result

    rate = await get_current_churn_rate(mock_db, tenant_id=1)
    assert rate == 0.0


@pytest.mark.asyncio
async def test_churn_trend_returns_monthly():
    """Churn trend returns data for each month."""
    from app.services.churn_calculator import get_churn_trend

    mock_db = AsyncMock()
    mock_result = MagicMock()
    mock_result.fetchall.return_value = [
        ("2026-01", 3.2, 2, 62),
        ("2026-02", 1.5, 1, 65),
    ]
    mock_db.execute.return_value = mock_result

    trend = await get_churn_trend(mock_db, tenant_id=1, months=2)
    assert len(trend) == 2
    assert trend[0].churn_rate == 3.2
    assert trend[0].churned == 2
    assert trend[1].active_start == 65
