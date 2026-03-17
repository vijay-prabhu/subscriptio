from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    database_url: str = "postgresql+asyncpg://subscriptio:subscriptio_dev@localhost:5432/subscriptio"
    cors_origins: list[str] = ["http://localhost:3000", "http://localhost:3001"]
    port: int = 8081

    model_config = {"env_prefix": "ANALYTICS_"}


settings = Settings()
