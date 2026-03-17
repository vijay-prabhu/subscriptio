# Stage 1: Build
FROM python:3.11-slim AS builder
WORKDIR /app
COPY analytics-service/pyproject.toml .
RUN pip install --no-cache-dir --target=/deps \
    "fastapi>=0.115.0" "uvicorn[standard]>=0.32.0" \
    "sqlalchemy[asyncio]>=2.0.0" "asyncpg>=0.30.0" \
    "pydantic>=2.0.0" "pydantic-settings>=2.0.0" "numpy>=1.26.0"

# Stage 2: Runtime
FROM python:3.11-slim
RUN groupadd -r app && useradd -r -g app app
WORKDIR /app
COPY --from=builder /deps /usr/local/lib/python3.11/site-packages
COPY analytics-service/app app
USER app
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=5s \
  CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:8081/health')" || exit 1
CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8081"]
