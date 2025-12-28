# Health Poster AI Platform
# منصة بوسترات التوعية الصحية

منصة مؤسسية رسمية لتوليد بوسترات التوعية الصحية بالذكاء الاصطناعي

**الجهة المالكة:** دائرة صحة كركوك – قطاع كركوك الأول – وحدة تعزيز الصحة

## 🏗️ Architecture Overview

This repository contains **two separate applications** that should be deployed independently:

1. **Backend (Java Spring Boot)** - REST API service
   - Location: `java-backend/`
   - Deploy on: **Railway**
   - Port: Uses `PORT` environment variable (Railway sets this automatically)

2. **Frontend (Next.js)** - Web application
   - Location: Root directory (`/app`, `/components`, etc.)
   - Deploy on: **Vercel** (recommended) or any Node.js hosting
   - Port: 3000 (default)

### Why Separate?

- **Backend**: Provides REST APIs for health statistics, poster generation, and data management
- **Frontend**: Provides user interface and connects to backend APIs
- **Database**: Shared PostgreSQL database (configured separately)

## 📋 Prerequisites

### For Backend (Java)
- Java 20 JDK
- Maven 3.8+
- PostgreSQL database

### For Frontend (Next.js)
- Node.js 20.x
- npm or yarn
- PostgreSQL database (shared with backend)

## 🚀 Quick Start

### Backend Setup (Java Spring Boot)

```bash
cd java-backend

# Install dependencies and build
mvn clean install

# Run locally
mvn spring-boot:run
```

The backend will run on `http://localhost:8080` (or the port specified by `PORT` env var).

### Frontend Setup (Next.js)

```bash
# Install dependencies
npm install

# Copy environment variables
cp env.local.example .env.local

# Edit .env.local and set:
# - DATABASE_URL (PostgreSQL connection string)
# - NEXTAUTH_URL (http://localhost:3000 for local)
# - NEXTAUTH_SECRET (generate a secure random string)
# - NEXT_PUBLIC_API_URL (http://localhost:8080 for local)

# Generate Prisma client
npm run db:generate

# Run database migrations
npm run db:migrate

# Start development server
npm run dev
```

The frontend will run on `http://localhost:3000`.

## 🚢 Deployment

### Backend Deployment on Railway

1. **Connect Repository to Railway**
   - Go to Railway dashboard
   - Click "New Project" → "Deploy from GitHub repo"
   - Select this repository

2. **Configure Railway Service**
   - Railway will detect `railway.json` and `nixpacks.toml` in root
   - These files are configured to build only `java-backend/`
   - **Important**: Railway should build from root, but the build commands will `cd` into `java-backend/`

3. **Add PostgreSQL Database**
   - In Railway dashboard, add a PostgreSQL service
   - Railway will automatically set `DATABASE_URL` environment variable

4. **Set Environment Variables**
   ```
   PORT=8080  # Railway sets this automatically, but you can override
   DATABASE_URL=<from PostgreSQL service>
   GEMINI_API_KEY=<your-gemini-api-key>
   ```

5. **Deploy**
   - Railway will automatically:
     - Install Java 20 and Maven
     - Run `mvn clean package -DskipTests`
     - Start the JAR file with `java -jar target/health-poster-ai-platform-1.0.0.jar`

6. **Verify Deployment**
   - Check Railway logs for: `✅ Successfully seeded 66 health topics into database`
   - Test API endpoint: `https://your-backend.railway.app/api/statistics/categories`

### Frontend Deployment on Vercel

1. **Connect Repository to Vercel**
   - Go to Vercel dashboard
   - Click "Add New Project"
   - Import this GitHub repository

2. **Configure Build Settings**
   - Framework Preset: **Next.js**
   - Root Directory: `/` (root)
   - Build Command: `npm run build`
   - Output Directory: `.next`

3. **Set Environment Variables**
   ```
   DATABASE_URL=<your-postgresql-connection-string>
   NEXTAUTH_URL=https://your-frontend.vercel.app
   NEXTAUTH_SECRET=<generate-secure-random-string>
   NEXT_PUBLIC_API_URL=https://your-backend.railway.app
   API_URL=https://your-backend.railway.app
   ```

4. **Deploy**
   - Vercel will automatically build and deploy
   - The frontend will connect to your Railway backend

### Alternative: Frontend on Railway

If you prefer to deploy frontend on Railway (not recommended, but possible):

1. Create a **separate Railway service** for frontend
2. Set Root Directory to `/` (root)
3. Use Node.js buildpack
4. Set start command: `npm start`
5. Set environment variables as above

**Note**: Railway is optimized for backend services. Vercel is better for Next.js frontends.

## 🔧 Configuration Files

### Root Directory Files (for Railway Backend)

- `railway.json` - Railway deployment configuration (builds java-backend)
- `nixpacks.toml` - Nixpacks build configuration (builds java-backend)
- `.railwayignore` - Excludes frontend files from Railway build

### Backend Configuration

- `java-backend/railway.json` - Alternative Railway config (if deploying from java-backend directory)
- `java-backend/nixpacks.toml` - Alternative Nixpacks config
- `java-backend/src/main/resources/application.properties` - Spring Boot configuration

### Frontend Configuration

- `next.config.js` - Next.js configuration
- `env.local.example` - Environment variables template
- `.env.local` - Local environment variables (not committed)

## 📡 API Endpoints

The backend exposes REST APIs under `/api/*`:

- `GET /api/statistics/categories` - Get health categories
- `GET /api/statistics/categories-topics` - Get all categories with topics
- `POST /api/statistics/daily` - Submit daily statistics
- `GET /api/posters/{id}` - Get poster details
- `POST /api/posters/generate` - Generate new poster
- And more... (see `java-backend/src/main/java/com/kirkukhealth/poster/controller/`)

## 🗄️ Database

Both frontend and backend share the same PostgreSQL database:

- **Backend**: Uses JPA/Hibernate to manage health statistics, posters, etc.
- **Frontend**: Uses Prisma to manage users, authentication, etc.

**Important**: Ensure both services use the same `DATABASE_URL`.

## 🔐 Security

- Backend CORS is configured to allow all origins (configure for production)
- Frontend uses NextAuth.js for authentication
- Passwords are hashed with bcrypt
- API endpoints require authentication (via NextAuth session)

## 🐛 Troubleshooting

### Backend 404 on Railway

If you see "Whitelabel Error Page" on Railway:

1. Check that Railway is building from root directory
2. Verify `railway.json` has correct build commands with `cd java-backend`
3. Check Railway logs for build errors
4. Verify `PORT` environment variable is set (Railway sets this automatically)

### Frontend Can't Connect to Backend

1. Verify `NEXT_PUBLIC_API_URL` is set correctly
2. Check CORS configuration in backend
3. Ensure backend is running and accessible
4. Check browser console for CORS errors

### Database Connection Issues

1. Verify `DATABASE_URL` is set correctly in both services
2. Check PostgreSQL service is running
3. Verify connection string format: `postgresql://user:password@host:port/database`

## 📝 Development

### Running Both Services Locally

1. **Terminal 1 - Backend:**
   ```bash
   cd java-backend
   mvn spring-boot:run
   ```

2. **Terminal 2 - Frontend:**
   ```bash
   npm run dev
   ```

3. **Access:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

### Database Migrations

**Backend (Java):**
- Uses Flyway migrations in `java-backend/src/main/resources/db/migration/`
- Automatically runs on startup with `spring.jpa.hibernate.ddl-auto=update`

**Frontend (Next.js):**
- Uses Prisma migrations in `prisma/migrations/`
- Run manually: `npm run db:migrate`

## 📚 Additional Documentation

- `java-backend/README.md` - Backend-specific documentation
- `java-backend/QUICK_START.md` - Quick start guide for backend
- `SETUP.md` - Detailed setup instructions

## 📄 License

This project is owned by دائرة صحة كركوك – قطاع كركوك الأول – وحدة تعزيز الصحة

## 🆘 Support

For questions and technical support, please contact the development team.
