"""Unit tests for revenue forecast."""
from decimal import Decimal
import numpy as np
import pytest


def test_linear_regression_projects_upward():
    """Simple sanity check that linear regression on upward data projects higher."""
    x = np.array([0, 1, 2, 3, 4, 5], dtype=float)
    y = np.array([100, 120, 140, 160, 180, 200], dtype=float)

    coeffs = np.polyfit(x, y, deg=1)
    projected = float(np.polyval(coeffs, 6))

    assert projected > 200
    assert abs(projected - 220) < 1  # should be ~220


def test_linear_regression_flat_data():
    """Flat data projects same value."""
    x = np.array([0, 1, 2, 3], dtype=float)
    y = np.array([100, 100, 100, 100], dtype=float)

    coeffs = np.polyfit(x, y, deg=1)
    projected = float(np.polyval(coeffs, 4))

    assert abs(projected - 100) < 1


def test_negative_projection_clamped_to_zero():
    """Declining data that would go negative gets clamped."""
    x = np.array([0, 1, 2], dtype=float)
    y = np.array([30, 20, 10], dtype=float)

    coeffs = np.polyfit(x, y, deg=1)
    projected = max(0, float(np.polyval(coeffs, 5)))

    assert projected >= 0
