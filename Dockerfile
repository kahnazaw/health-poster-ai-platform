FROM node:18-alpine AS base

# Install OpenSSL and required libraries for Prisma
RUN apk add --no-cache \
    openssl \
    libc6-compat \
    openssl-dev \
    ca-certificates

# Install dependencies only when needed
FROM base AS deps
WORKDIR /app

# Install dependencies based on the preferred package manager
COPY package.json package-lock.json* ./
RUN npm ci

# Rebuild the source code only when needed
FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Generate Prisma Client with musl binary target
ENV PRISMA_CLI_BINARY_TARGETS=linux-musl-openssl-3.0.x
RUN npx prisma generate

# Build Next.js
RUN npm run build

# Production image, copy all the files and run next
FROM base AS runner
WORKDIR /app

ENV NODE_ENV=production

# Install OpenSSL in runner stage (critical for Prisma runtime)
RUN apk add --no-cache \
    openssl \
    libc6-compat \
    ca-certificates

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

# Copy Prisma files and engine for migrations
COPY --from=builder /app/prisma ./prisma
COPY --from=builder /app/node_modules/.prisma ./node_modules/.prisma
COPY --from=builder /app/node_modules/@prisma ./node_modules/@prisma

# Set environment variables for Prisma
ENV PRISMA_QUERY_ENGINE_LIBRARY=/app/node_modules/.prisma/client/libquery_engine-linux-musl-openssl-3.0.x.so.node
ENV PRISMA_QUERY_ENGINE_BINARY=/app/node_modules/.prisma/client/query-engine-linux-musl-openssl-3.0.x
ENV PRISMA_MIGRATION_ENGINE_BINARY=/app/node_modules/.prisma/client/migration-engine-linux-musl-openssl-3.0.x
ENV PRISMA_INTROSPECTION_ENGINE_BINARY=/app/node_modules/.prisma/client/introspection-engine-linux-musl-openssl-3.0.x
ENV PRISMA_FMT_BINARY=/app/node_modules/.prisma/client/prisma-fmt-linux-musl-openssl-3.0.x

# Verify DATABASE_URL is set (Railway provides this automatically)
# The app will read DATABASE_URL from environment variables

USER nextjs

EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

# Run migrations and start
CMD ["sh", "-c", "npx prisma migrate deploy && node server.js"]

