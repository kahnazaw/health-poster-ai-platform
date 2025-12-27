# Railway Deployment Guide - Production Ready

Complete step-by-step guide for deploying `health-poster-ai-platform` to Railway.

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Initial Setup](#initial-setup)
3. [Environment Variables](#environment-variables)
4. [Database Configuration](#database-configuration)
5. [Deployment Steps](#deployment-steps)
6. [Connection Pooling](#connection-pooling)
7. [Migrations](#migrations)
8. [Troubleshooting](#troubleshooting)
9. [Production Checklist](#production-checklist)

---

## Prerequisites

- Railway account ([railway.app](https://railway.app))
- GitHub repository connected to Railway
- Node.js 18+ installed locally (for local development)
- PostgreSQL knowledge (basic)

---

## Initial Setup

### Step 1: Create Railway Project

1. Go to [Railway Dashboard](https://railway.app/dashboard)
2. Click **"New Project"**
3. Select **"Deploy from GitHub repo"**
4. Choose your `health-poster-ai-platform` repository
5. Railway will automatically detect the project

### Step 2: Add PostgreSQL Service

1. In your Railway project, click **"+ New"**
2. Select **"Database"** → **"Add PostgreSQL"**
3. Railway will automatically provision a PostgreSQL instance
4. **Important:** Railway automatically creates `DATABASE_URL` environment variable

### Step 3: Configure Build Settings

Railway will automatically detect `nixpacks.toml` and use it for building.

**No manual configuration needed** - the project is pre-configured!

---

## Environment Variables

### Required Variables

Set these in Railway Dashboard → Your Service → Variables:

| Variable | Description | Example | Required |
|----------|-------------|---------|----------|
| `DATABASE_URL` | PostgreSQL connection (auto-created) | `postgresql://postgres:pass@postgres.railway.internal:5432/railway` | ✅ Yes |
| `NEXTAUTH_URL` | Your app's public URL | `https://your-app.railway.app` | ✅ Yes |
| `NEXTAUTH_SECRET` | Secret for NextAuth sessions | Generate with `openssl rand -base64 32` | ✅ Yes |

### Optional Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DIRECT_URL` | Direct DB connection (for pooling) | Same as `DATABASE_URL` | ❌ No |
| `ADMIN_EMAIL` | Admin account email | `admin@kirkuk.health` | ❌ No |
| `ADMIN_PASSWORD` | Admin account password | `@#Eng1990` | ❌ No |
| `OPENAI_API_KEY` | OpenAI API key (for AI features) | - | ❌ No |

### How to Set Environment Variables

1. Go to Railway Dashboard → Your Service
2. Click **"Variables"** tab
3. Click **"+ New Variable"**
4. Enter variable name and value
5. Click **"Add"**

### Generate NEXTAUTH_SECRET

**On Linux/Mac:**
```bash
openssl rand -base64 32
```

**On Windows (PowerShell):**
```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

**Or use Railway's built-in generator:**
- Railway Dashboard → Variables → Click on `NEXTAUTH_SECRET` → Generate

---

## Database Configuration

### Understanding Railway Database URLs

Railway provides two types of database URLs:

#### 1. Internal URL (for Railway deployments)
- Format: `postgresql://postgres:password@postgres.railway.internal:5432/railway`
- **Use this for:** Railway deployments (automatic)
- **Access:** Only accessible within Railway's network
- **Auto-created:** Railway sets this as `DATABASE_URL` automatically

#### 2. Public TCP Proxy URL (for local migrations)
- Format: `postgresql://postgres:password@containers-us-west-xxx.railway.app:5432/railway`
- **Use this for:** Running migrations from your local machine
- **Access:** Publicly accessible (requires Public Gateway)
- **How to get:** Railway Dashboard → PostgreSQL Service → Connect tab

### Connection Pooling

This project supports Railway's connection pooling out of the box.

**Standard Setup (No Pooling):**
- Set only `DATABASE_URL`
- Prisma uses this for all operations

**With Connection Pooling:**
- `DATABASE_URL` = Pooled connection URL (for queries)
- `DIRECT_URL` = Direct connection URL (for migrations)
- Both should point to the same database

**Note:** For most Railway deployments, you don't need connection pooling. Only set `DIRECT_URL` if you're using a connection pooler like PgBouncer.

---

## Deployment Steps

### Automatic Deployment (Recommended)

1. **Push to GitHub:**
   ```bash
   git add .
   git commit -m "Ready for Railway deployment"
   git push origin main
   ```

2. **Railway will automatically:**
   - Detect the push
   - Start building using `nixpacks.toml`
   - Run migrations on startup
   - Deploy your application

3. **Monitor the deployment:**
   - Go to Railway Dashboard → Your Service → Deployments
   - Watch the build logs in real-time

### Build Process (What Happens)

The build process runs these steps (configured in `nixpacks.toml`):

1. **Install dependencies:**
   ```bash
   npm install
   ```
   - Installs all npm packages
   - Runs `postinstall` script (generates Prisma Client)

2. **Generate Prisma Client:**
   ```bash
   SKIP_ENV_VALIDATION=1 npx prisma generate
   ```
   - ✅ **No database connection required**
   - Generates TypeScript types
   - Creates Prisma Client

3. **Build Next.js:**
   ```bash
   SKIP_ENV_VALIDATION=1 npm run build
   ```
   - ✅ **No database connection required**
   - Compiles TypeScript
   - Optimizes assets
   - Creates production build

### Startup Process

After successful build, Railway runs:

```bash
npx prisma migrate deploy && npm run start
```

This:
1. ✅ Connects to database (using `DATABASE_URL`)
2. ✅ Runs pending migrations
3. ✅ Starts Next.js server

---

## Migrations

### Automatic Migrations (Production)

Migrations run automatically on every deployment:

- **When:** During startup (after build)
- **Command:** `npx prisma migrate deploy`
- **Behavior:** Applies pending migrations only
- **Safe:** Won't apply migrations that are already applied

### Manual Migrations (Local Development)

To run migrations from your local machine:

#### Step 1: Enable Public Gateway

1. Go to Railway Dashboard → PostgreSQL Service
2. Click **"Settings"** → **"Network"**
3. Enable **"Public Gateway"**
4. Copy the **Public TCP Proxy URL** from **"Connect"** tab

#### Step 2: Run Migration

**Option A: Using environment variable**
```bash
DATABASE_URL="<public-tcp-proxy-url>" npx prisma migrate dev
```

**Option B: Create local .env file**
```bash
# .env.local
DATABASE_URL="<public-tcp-proxy-url>"
```

Then run:
```bash
npx prisma migrate dev
```

#### Step 3: Create New Migration

```bash
# Make changes to prisma/schema.prisma
npx prisma migrate dev --name your_migration_name
```

This will:
- Create migration files
- Apply migration to your local/remote database
- Regenerate Prisma Client

### Migration Commands Reference

| Command | Use Case | When to Use |
|---------|----------|-------------|
| `npx prisma migrate dev` | Create and apply migrations | Local development |
| `npx prisma migrate deploy` | Apply pending migrations | Production (automatic) |
| `npx prisma migrate status` | Check migration status | Anytime |
| `npx prisma db push` | Push schema without migrations | Quick prototyping |

---

## Connection Pooling

### When to Use Connection Pooling

Use connection pooling if:
- You have high traffic (>100 concurrent connections)
- You're experiencing connection limit issues
- Railway recommends it for your use case

### Setup with Connection Pooling

1. **Set up a connection pooler** (e.g., PgBouncer) or use Railway's pooling service

2. **Configure environment variables:**
   ```
   DATABASE_URL=postgresql://pooled-connection-url
   DIRECT_URL=postgresql://direct-connection-url
   ```

3. **How it works:**
   - `DATABASE_URL` → Used for queries (goes through pool)
   - `DIRECT_URL` → Used for migrations (bypasses pool)

### Without Connection Pooling (Default)

Just set `DATABASE_URL`. Prisma will use it for everything.

**This is the recommended setup for most applications.**

---

## Troubleshooting

### Error: P1001 Can't reach database server

**During Build:**
- ✅ **This should NOT happen** - build doesn't connect to DB
- **Fix:** Ensure `SKIP_ENV_VALIDATION=1` is set in build phase (already configured)

**During Runtime:**
- **Check:** `DATABASE_URL` is set correctly
- **Check:** PostgreSQL service is running in Railway
- **Check:** Service logs for connection errors
- **Fix:** Verify Railway PostgreSQL service is provisioned and running

### Error: Invalid DATABASE_URL

- **Check:** URL format: `postgresql://user:password@host:port/database`
- **Check:** Special characters in password (URL encode if needed)
- **Fix:** Copy URL directly from Railway Dashboard → Connect tab

### Build Fails: "next: not found"

- ✅ **Already fixed** - using `npx next build` in package.json
- **If still happens:** Check that `next` is in `dependencies` (not `devDependencies`)

### Migrations Not Running

- **Check:** `nixpacks.toml` has correct start command
- **Check:** Railway logs for migration errors
- **Fix:** Manually run `railway run npx prisma migrate deploy`

### Local Migrations Not Working

1. **Enable Public Gateway:**
   - Railway Dashboard → PostgreSQL → Settings → Network
   - Enable "Public Gateway"

2. **Use Public TCP Proxy URL:**
   - Not the internal URL
   - Copy from Connect tab

3. **Run migration:**
   ```bash
   DATABASE_URL="<public-url>" npx prisma migrate dev
   ```

### Application Won't Start

1. **Check Railway logs:**
   - Railway Dashboard → Your Service → Logs

2. **Common issues:**
   - Missing `NEXTAUTH_SECRET` → Set it in Variables
   - Missing `NEXTAUTH_URL` → Set to your Railway app URL
   - Database connection failed → Check `DATABASE_URL`

3. **Verify environment variables:**
   - All required variables are set
   - Values are correct (no typos)

---

## Production Checklist

Before going live, verify:

### ✅ Environment Variables
- [ ] `DATABASE_URL` is set (auto-created by Railway)
- [ ] `NEXTAUTH_URL` matches your Railway app URL
- [ ] `NEXTAUTH_SECRET` is set and secure
- [ ] `ADMIN_EMAIL` and `ADMIN_PASSWORD` are set (optional)
- [ ] `OPENAI_API_KEY` is set (if using AI features)

### ✅ Database
- [ ] PostgreSQL service is running
- [ ] Migrations have been applied
- [ ] Admin account exists (check with seed script)

### ✅ Application
- [ ] Build completes successfully
- [ ] Application starts without errors
- [ ] Health check endpoint works (if implemented)
- [ ] Can access login page

### ✅ Security
- [ ] `NEXTAUTH_SECRET` is strong and unique
- [ ] Default admin password is changed
- [ ] Public Gateway is disabled (if not needed)
- [ ] Environment variables are not exposed in logs

### ✅ Monitoring
- [ ] Railway logs are accessible
- [ ] Error tracking is set up (optional)
- [ ] Database connection is stable

---

## Quick Reference

### Railway CLI Commands

```bash
# Install Railway CLI
npm i -g @railway/cli

# Login
railway login

# Link project
railway link

# Run migrations
railway run npx prisma migrate deploy

# Run seed
railway run npm run seed

# View logs
railway logs

# Open shell
railway shell
```

### Common Commands

```bash
# Check migration status
npx prisma migrate status

# Generate Prisma Client (local)
npm run db:generate

# Create new migration
npx prisma migrate dev --name migration_name

# Seed database
npm run seed
```

---

## Support

If you encounter issues:

1. **Check Railway logs:** Dashboard → Service → Logs
2. **Check build logs:** Dashboard → Service → Deployments
3. **Verify environment variables:** Dashboard → Service → Variables
4. **Review this guide:** Ensure all steps are followed correctly

---

## Additional Resources

- [Railway Documentation](https://docs.railway.app)
- [Prisma Documentation](https://www.prisma.io/docs)
- [Next.js Deployment](https://nextjs.org/docs/deployment)

---

**Last Updated:** 2025-01-01
**Project:** health-poster-ai-platform
**Version:** 1.0.0
