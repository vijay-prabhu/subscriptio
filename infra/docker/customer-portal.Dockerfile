# Stage 1: Dependencies
FROM node:20-alpine AS deps
WORKDIR /app
COPY customer-portal/package.json customer-portal/package-lock.json ./
RUN npm ci --ignore-scripts

# Stage 2: Build
FROM node:20-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY customer-portal/ .
RUN npx nuxi@3.14.0 build

# Stage 3: Runtime
FROM node:20-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=builder /app/.output .output
USER app
EXPOSE 3000
ENV HOST=0.0.0.0
ENV PORT=3000
HEALTHCHECK --interval=30s --timeout=5s \
  CMD curl -f http://localhost:3000/ || exit 1
CMD ["node", ".output/server/index.mjs"]
