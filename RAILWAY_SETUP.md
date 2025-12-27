# Railway Deployment Guide / دليل النشر على Railway

**English** | [العربية](#العربية)

---

# English

## 🚀 Complete Railway Deployment Guide - Cost Optimized

This guide will help you deploy `health-poster-ai-platform` to Railway **without P1001 errors** and **save money** by using Railway's free internal network.

---

## 💰 Why This Setup Saves Money

### The Problem with Public URLs
- Railway charges **Egress fees** for data transferred through Public TCP Proxy URLs
- Public URLs use the internet, which costs money
- Internal URLs are **completely FREE** and faster

### Our Solution
- **DATABASE_URL** = Private Internal URL (`.railway.internal`) → **FREE** ✅
- **DIRECT_URL** = Public TCP Proxy URL → Only used when needed for migrations
- This setup uses the free internal network for 99% of operations

**Result:** You save money and get faster connections! 🎉

---

## 📋 Table of Contents

1. [Understanding the P1001 Error](#understanding-the-p1001-error)
2. [Environment Variables Setup (Cost-Optimized)](#environment-variables-setup-cost-optimized)
3. [Database Configuration](#database-configuration)
4. [Step-by-Step Deployment](#step-by-step-deployment)
5. [Troubleshooting](#troubleshooting)

---

## Understanding the P1001 Error

**Error:** `P1001: Can't reach database server at postgres.railway.internal:5432`

**Why it happens:**
- Prisma tries to connect to the database during the build phase
- The database is not accessible during build (only at runtime)
- Missing or incorrect `DATABASE_URL` configuration

**Solution:**
- Use `SKIP_ENV_VALIDATION=1` during build (prevents connection attempts)
- Set up `DATABASE_URL` (private/internal) and `DIRECT_URL` (public) correctly
- Build script runs `prisma generate` without connecting to database

---

## Environment Variables Setup (Cost-Optimized)

### Required Variables in Railway Dashboard

Go to **Railway Dashboard → Your Service → Variables** and add:

#### 1. DATABASE_URL (Private Internal URL - FREE) ✅

**What it is:**
- The **private internal** database connection URL
- Uses Railway's internal network (`.railway.internal`)
- **Completely FREE** - no egress fees
- Used for 99% of database operations (queries, reads, writes)

**How to get it:**
1. Railway Dashboard → Your PostgreSQL Service
2. Click **"Connect"** tab
3. Look for **"Connection URL"** or **"Private Network"**
4. Copy the URL that contains `.railway.internal`
5. Format: `postgresql://postgres:password@postgres.railway.internal:5432/railway`

**In Railway Variables:**
- **Name:** `DATABASE_URL`
- **Value:** `postgresql://postgres:password@postgres.railway.internal:5432/railway`
- **Note:** This is the **FREE internal URL** - use this for most operations

**Why this saves money:**
- Uses Railway's internal network (no internet traffic)
- No egress fees
- Faster connection (internal network is faster)

#### 2. DIRECT_URL (Public TCP Proxy URL - Use Sparingly)

**What it is:**
- The **public** database connection URL
- Uses Railway's TCP Proxy (goes through internet)
- **May incur egress fees** if used frequently
- Only used for migrations and schema operations when needed

**How to get it:**
1. Railway Dashboard → Your PostgreSQL Service
2. Click **"Connect"** tab
3. Look for **"Public TCP Proxy URL"** or **"Connection URL"** (public)
4. Copy the URL that contains `containers-us-west-xxx.railway.app` or similar
5. Format: `postgresql://postgres:password@containers-us-west-xxx.railway.app:5432/railway`

**In Railway Variables:**
- **Name:** `DIRECT_URL`
- **Value:** `postgresql://postgres:password@containers-us-west-xxx.railway.app:5432/railway`
- **Note:** This is the **public URL** - only used when internal URL doesn't work for migrations

**When it's used:**
- Prisma migrations (if internal URL has issues)
- Schema operations
- **Not used for regular queries** (saves money!)

**Cost optimization tip:**
- Set this variable, but Prisma will prefer `DATABASE_URL` for most operations
- Only uses `DIRECT_URL` when absolutely necessary

#### 3. NEXTAUTH_URL

**What it is:**
- Your application's public URL
- Required for NextAuth.js authentication

**How to set it:**
1. After deployment, Railway provides a URL like: `https://your-app.railway.app`
2. Or use your custom domain if configured

**In Railway Variables:**
- **Name:** `NEXTAUTH_URL`
- **Value:** `https://your-app.railway.app`

#### 4. NEXTAUTH_SECRET

**What it is:**
- Secret key for encrypting NextAuth sessions
- Must be a random, secure string

**How to generate:**
```bash
# Linux/Mac
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

**In Railway Variables:**
- **Name:** `NEXTAUTH_SECRET`
- **Value:** (Generated secret, e.g., `aBc123XyZ...`)

#### 5. Optional Variables

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `ADMIN_EMAIL` | Admin account email | `admin@kirkuk.health` |
| `ADMIN_PASSWORD` | Admin account password | `@#Eng1990` |
| `OPENAI_API_KEY` | OpenAI API key (for AI features) | - |

---

## Database Configuration

### Cost-Optimized Setup (Recommended) ✅

**This is the setup that saves you money:**

1. **DATABASE_URL** = Private Internal URL (`.railway.internal`)
   - Used for: All regular queries, reads, writes
   - Cost: **FREE** ✅
   - Speed: **Fast** (internal network)

2. **DIRECT_URL** = Public TCP Proxy URL
   - Used for: Migrations only (when needed)
   - Cost: May incur fees if used frequently
   - Speed: Slower (goes through internet)

**How Prisma uses them:**
- Regular operations → Uses `DATABASE_URL` (free internal network)
- Migrations → Uses `DIRECT_URL` if `DATABASE_URL` doesn't work for migrations
- Result: 99% of operations use free internal network

**Why this saves money:**
- Most database operations use the free internal network
- Public URL only used when absolutely necessary
- No unnecessary egress fees

---

## Step-by-Step Deployment

### Step 1: Create Railway Project

1. Go to [Railway Dashboard](https://railway.app/dashboard)
2. Click **"New Project"**
3. Select **"Deploy from GitHub repo"**
4. Choose your repository
5. Railway will detect the project automatically

### Step 2: Add PostgreSQL Service

1. In your Railway project, click **"+ New"**
2. Select **"Database"** → **"Add PostgreSQL"**
3. Railway will provision PostgreSQL automatically
4. **Note:** Railway automatically creates `DATABASE_URL` variable (but check it's the internal URL)

### Step 3: Configure Environment Variables (IMPORTANT!)

1. Go to Railway Dashboard → Your Service → **"Variables"** tab
2. Add the following variables:

**a) DATABASE_URL (Private - FREE):**
```
Name: DATABASE_URL
Value: postgresql://postgres:password@postgres.railway.internal:5432/railway
```
- Get this from: PostgreSQL Service → Connect → **Private Network URL**
- Must contain `.railway.internal`
- This is the **FREE internal URL**

**b) DIRECT_URL (Public - Use Sparingly):**
```
Name: DIRECT_URL
Value: postgresql://postgres:password@containers-us-west-xxx.railway.app:5432/railway
```
- Get this from: PostgreSQL Service → Connect → **Public TCP Proxy URL**
- Contains `railway.app` domain
- Only used when internal URL doesn't work

**c) NEXTAUTH_URL:**
```
Name: NEXTAUTH_URL
Value: https://your-app.railway.app
```

**d) NEXTAUTH_SECRET:**
```
Name: NEXTAUTH_SECRET
Value: (Generate using openssl rand -base64 32)
```

### Step 4: Deploy

1. Push your code to GitHub:
   ```bash
   git add .
   git commit -m "Ready for Railway deployment"
   git push origin main
   ```

2. Railway will automatically:
   - Detect the push
   - Build using `nixpacks.toml`
   - Run migrations on startup
   - Deploy your application

3. Monitor the deployment:
   - Railway Dashboard → Your Service → Deployments
   - Watch build logs in real-time

### Step 5: Verify Deployment

1. Check build logs for:
   - ✅ `SKIP_ENV_VALIDATION=1` is set
   - ✅ `prisma generate` completes without errors
   - ✅ `next build` completes successfully

2. Check startup logs for:
   - ✅ `prisma migrate deploy` runs successfully
   - ✅ Application starts without errors

3. Visit your app URL:
   - Should load without errors
   - Login page should be accessible

---

## Troubleshooting

### Error: P1001 Can't reach database server

**During Build:**
- ✅ **Should NOT happen** - build doesn't connect to DB
- **Check:** `SKIP_ENV_VALIDATION=1` is set in build phase
- **Check:** Build script uses `SKIP_ENV_VALIDATION=1 npx prisma generate`
- **Fix:** Verify `nixpacks.toml` has correct environment variables

**During Runtime:**
- **Check:** `DATABASE_URL` is set correctly (must be internal URL)
- **Check:** PostgreSQL service is running
- **Check:** Service logs for connection errors
- **Fix:** Verify Railway PostgreSQL service is provisioned

### Error: Invalid DATABASE_URL

- **Check:** URL format: `postgresql://user:password@host:port/database`
- **Check:** Special characters in password (URL encode if needed)
- **Fix:** Copy URL directly from Railway Dashboard → Connect tab
- **Important:** Make sure you're using the **internal URL** (`.railway.internal`)

### Warning: Egress Fees

- **Cause:** Using public TCP Proxy URL too frequently
- **Solution:** Ensure `DATABASE_URL` is set to internal URL (`.railway.internal`)
- **Check:** Most operations should use `DATABASE_URL`, not `DIRECT_URL`

---

## Cost Optimization Tips

1. ✅ **Always use internal URL for DATABASE_URL**
   - Free, fast, no egress fees

2. ✅ **Set DIRECT_URL but don't worry**
   - Prisma will prefer `DATABASE_URL` for most operations
   - Only uses `DIRECT_URL` when necessary

3. ✅ **Monitor your usage**
   - Check Railway dashboard for egress usage
   - If you see high egress, verify `DATABASE_URL` is internal

---

# العربية

## 🚀 دليل النشر الكامل على Railway - محسّن للتكلفة

سيساعدك هذا الدليل على نشر `health-poster-ai-platform` على Railway **بدون أخطاء P1001** و **توفير المال** باستخدام الشبكة الداخلية المجانية لـ Railway.

---

## 💰 لماذا هذا الإعداد يوفر المال

### مشكلة الروابط العامة
- Railway تفرض **رسوم Egress** على البيانات المنقولة عبر Public TCP Proxy URLs
- الروابط العامة تستخدم الإنترنت، مما يكلف المال
- الروابط الداخلية **مجانية تماماً** وأسرع

### حلنا
- **DATABASE_URL** = رابط داخلي خاص (`.railway.internal`) → **مجاني** ✅
- **DIRECT_URL** = رابط Public TCP Proxy → يُستخدم فقط عند الحاجة للهجرات
- هذا الإعداد يستخدم الشبكة الداخلية المجانية لـ 99% من العمليات

**النتيجة:** توفر المال وتحصل على اتصالات أسرع! 🎉

---

## 📋 جدول المحتويات

1. [فهم خطأ P1001](#فهم-خطأ-p1001)
2. [إعداد متغيرات البيئة (محسّن للتكلفة)](#إعداد-متغيرات-البيئة-محسّن-للتكلفة)
3. [تكوين قاعدة البيانات](#تكوين-قاعدة-البيانات)
4. [خطوات النشر خطوة بخطوة](#خطوات-النشر-خطوة-بخطوة)
5. [استكشاف الأخطاء](#استكشاف-الأخطاء)

---

## فهم خطأ P1001

**الخطأ:** `P1001: Can't reach database server at postgres.railway.internal:5432`

**لماذا يحدث:**
- Prisma يحاول الاتصال بقاعدة البيانات أثناء مرحلة البناء
- قاعدة البيانات غير متاحة أثناء البناء (فقط في وقت التشغيل)
- `DATABASE_URL` مفقود أو غير صحيح

**الحل:**
- استخدام `SKIP_ENV_VALIDATION=1` أثناء البناء (يمنع محاولات الاتصال)
- إعداد `DATABASE_URL` (خاص/داخلي) و `DIRECT_URL` (عام) بشكل صحيح
- سكربت البناء يشغل `prisma generate` دون الاتصال بقاعدة البيانات

---

## إعداد متغيرات البيئة (محسّن للتكلفة)

### المتغيرات المطلوبة في لوحة تحكم Railway

اذهب إلى **لوحة تحكم Railway → خدمتك → Variables** وأضف:

#### 1. DATABASE_URL (رابط داخلي خاص - مجاني) ✅

**ما هو:**
- رابط الاتصال **الداخلي الخاص** بقاعدة البيانات
- يستخدم الشبكة الداخلية لـ Railway (`.railway.internal`)
- **مجاني تماماً** - لا رسوم egress
- يُستخدم لـ 99% من عمليات قاعدة البيانات (استعلامات، قراءات، كتابات)

**كيفية الحصول عليه:**
1. لوحة تحكم Railway → خدمة PostgreSQL الخاصة بك
2. انقر على تبويب **"Connect"**
3. ابحث عن **"Connection URL"** أو **"Private Network"**
4. انسخ الرابط الذي يحتوي على `.railway.internal`
5. الصيغة: `postgresql://postgres:password@postgres.railway.internal:5432/railway`

**في متغيرات Railway:**
- **الاسم:** `DATABASE_URL`
- **القيمة:** `postgresql://postgres:password@postgres.railway.internal:5432/railway`
- **ملاحظة:** هذا هو **الرابط الداخلي المجاني** - استخدمه لمعظم العمليات

**لماذا هذا يوفر المال:**
- يستخدم الشبكة الداخلية لـ Railway (لا حركة مرور عبر الإنترنت)
- لا رسوم egress
- اتصال أسرع (الشبكة الداخلية أسرع)

#### 2. DIRECT_URL (رابط Public TCP Proxy - استخدمه باعتدال)

**ما هو:**
- رابط الاتصال **العام** بقاعدة البيانات
- يستخدم TCP Proxy لـ Railway (يمر عبر الإنترنت)
- **قد يترتب عليه رسوم egress** إذا استُخدم بكثرة
- يُستخدم فقط للهجرات وعمليات المخطط عند الحاجة

**كيفية الحصول عليه:**
1. لوحة تحكم Railway → خدمة PostgreSQL الخاصة بك
2. انقر على تبويب **"Connect"**
3. ابحث عن **"Public TCP Proxy URL"** أو **"Connection URL"** (عام)
4. انسخ الرابط الذي يحتوي على `containers-us-west-xxx.railway.app` أو مشابه
5. الصيغة: `postgresql://postgres:password@containers-us-west-xxx.railway.app:5432/railway`

**في متغيرات Railway:**
- **الاسم:** `DIRECT_URL`
- **القيمة:** `postgresql://postgres:password@containers-us-west-xxx.railway.app:5432/railway`
- **ملاحظة:** هذا هو **الرابط العام** - يُستخدم فقط عندما لا يعمل الرابط الداخلي للهجرات

**متى يُستخدم:**
- هجرات Prisma (إذا كان الرابط الداخلي به مشاكل)
- عمليات المخطط
- **لا يُستخدم للاستعلامات العادية** (يوفر المال!)

**نصيحة تحسين التكلفة:**
- اضبط هذا المتغير، لكن Prisma سيفضل `DATABASE_URL` لمعظم العمليات
- يستخدم `DIRECT_URL` فقط عند الضرورة القصوى

#### 3. NEXTAUTH_URL

**ما هو:**
- الرابط العام لتطبيقك
- مطلوب لمصادقة NextAuth.js

**كيفية ضبطه:**
1. بعد النشر، يوفر Railway رابطاً مثل: `https://your-app.railway.app`
2. أو استخدم نطاقك المخصص إذا كان مُعداً

**في متغيرات Railway:**
- **الاسم:** `NEXTAUTH_URL`
- **القيمة:** `https://your-app.railway.app`

#### 4. NEXTAUTH_SECRET

**ما هو:**
- مفتاح سري لتشفير جلسات NextAuth
- يجب أن يكون سلسلة عشوائية وآمنة

**كيفية توليده:**
```bash
# Linux/Mac
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

**في متغيرات Railway:**
- **الاسم:** `NEXTAUTH_SECRET`
- **القيمة:** (المفتاح المُولد، مثال: `aBc123XyZ...`)

#### 5. متغيرات اختيارية

| المتغير | الوصف | القيمة الافتراضية |
|---------|-------|-------------------|
| `ADMIN_EMAIL` | بريد حساب المدير | `admin@kirkuk.health` |
| `ADMIN_PASSWORD` | كلمة مرور حساب المدير | `@#Eng1990` |
| `OPENAI_API_KEY` | مفتاح OpenAI API (للميزات الذكية) | - |

---

## تكوين قاعدة البيانات

### الإعداد المحسّن للتكلفة (موصى به) ✅

**هذا هو الإعداد الذي يوفر لك المال:**

1. **DATABASE_URL** = رابط داخلي خاص (`.railway.internal`)
   - يُستخدم لـ: جميع الاستعلامات العادية، القراءات، الكتابات
   - التكلفة: **مجاني** ✅
   - السرعة: **سريع** (شبكة داخلية)

2. **DIRECT_URL** = رابط Public TCP Proxy
   - يُستخدم لـ: الهجرات فقط (عند الحاجة)
   - التكلفة: قد يترتب عليه رسوم إذا استُخدم بكثرة
   - السرعة: أبطأ (يمر عبر الإنترنت)

**كيف يستخدم Prisma هذه الروابط:**
- العمليات العادية → يستخدم `DATABASE_URL` (شبكة داخلية مجانية)
- الهجرات → يستخدم `DIRECT_URL` إذا لم يعمل `DATABASE_URL` للهجرات
- النتيجة: 99% من العمليات تستخدم الشبكة الداخلية المجانية

**لماذا هذا يوفر المال:**
- معظم عمليات قاعدة البيانات تستخدم الشبكة الداخلية المجانية
- الرابط العام يُستخدم فقط عند الضرورة القصوى
- لا رسوم egress غير ضرورية

---

## خطوات النشر خطوة بخطوة

### الخطوة 1: إنشاء مشروع Railway

1. اذهب إلى [لوحة تحكم Railway](https://railway.app/dashboard)
2. انقر على **"New Project"**
3. اختر **"Deploy from GitHub repo"**
4. اختر مستودعك
5. سيكتشف Railway المشروع تلقائياً

### الخطوة 2: إضافة خدمة PostgreSQL

1. في مشروع Railway، انقر على **"+ New"**
2. اختر **"Database"** → **"Add PostgreSQL"**
3. سيقوم Railway بتوفير PostgreSQL تلقائياً
4. **ملاحظة:** Railway ينشئ متغير `DATABASE_URL` تلقائياً (لكن تحقق من أنه الرابط الداخلي)

### الخطوة 3: تكوين متغيرات البيئة (مهم!)

1. اذهب إلى لوحة تحكم Railway → خدمتك → تبويب **"Variables"**
2. أضف المتغيرات التالية:

**أ) DATABASE_URL (خاص - مجاني):**
```
الاسم: DATABASE_URL
القيمة: postgresql://postgres:password@postgres.railway.internal:5432/railway
```
- احصل عليه من: خدمة PostgreSQL → Connect → **رابط الشبكة الخاصة**
- يجب أن يحتوي على `.railway.internal`
- هذا هو **الرابط الداخلي المجاني**

**ب) DIRECT_URL (عام - استخدمه باعتدال):**
```
الاسم: DIRECT_URL
القيمة: postgresql://postgres:password@containers-us-west-xxx.railway.app:5432/railway
```
- احصل عليه من: خدمة PostgreSQL → Connect → **رابط Public TCP Proxy**
- يحتوي على نطاق `railway.app`
- يُستخدم فقط عندما لا يعمل الرابط الداخلي

**ج) NEXTAUTH_URL:**
```
الاسم: NEXTAUTH_URL
القيمة: https://your-app.railway.app
```

**د) NEXTAUTH_SECRET:**
```
الاسم: NEXTAUTH_SECRET
القيمة: (أنشئه باستخدام openssl rand -base64 32)
```

### الخطوة 4: النشر

1. ادفع الكود إلى GitHub:
   ```bash
   git add .
   git commit -m "Ready for Railway deployment"
   git push origin main
   ```

2. سيقوم Railway تلقائياً بـ:
   - اكتشاف الدفع
   - البناء باستخدام `nixpacks.toml`
   - تشغيل الهجرات عند البدء
   - نشر تطبيقك

3. راقب النشر:
   - لوحة تحكم Railway → خدمتك → Deployments
   - شاهد سجلات البناء في الوقت الفعلي

### الخطوة 5: التحقق من النشر

1. تحقق من سجلات البناء:
   - ✅ `SKIP_ENV_VALIDATION=1` مضبوط
   - ✅ `prisma generate` يكتمل دون أخطاء
   - ✅ `next build` يكتمل بنجاح

2. تحقق من سجلات البدء:
   - ✅ `prisma migrate deploy` يعمل بنجاح
   - ✅ التطبيق يبدأ دون أخطاء

3. زر رابط تطبيقك:
   - يجب أن يحمل دون أخطاء
   - صفحة تسجيل الدخول يجب أن تكون قابلة للوصول

---

## استكشاف الأخطاء

### خطأ: P1001 Can't reach database server

**أثناء البناء:**
- ✅ **يجب ألا يحدث** - البناء لا يتصل بقاعدة البيانات
- **تحقق:** `SKIP_ENV_VALIDATION=1` مضبوط في مرحلة البناء
- **تحقق:** سكربت البناء يستخدم `SKIP_ENV_VALIDATION=1 npx prisma generate`
- **الحل:** تحقق من أن `nixpacks.toml` يحتوي على متغيرات البيئة الصحيحة

**أثناء التشغيل:**
- **تحقق:** `DATABASE_URL` مضبوط بشكل صحيح (يجب أن يكون رابط داخلي)
- **تحقق:** خدمة PostgreSQL تعمل
- **تحقق:** سجلات الخدمة لأخطاء الاتصال
- **الحل:** تحقق من أن خدمة PostgreSQL في Railway مُعدة

### خطأ: Invalid DATABASE_URL

- **تحقق:** صيغة الرابط: `postgresql://user:password@host:port/database`
- **تحقق:** الأحرف الخاصة في كلمة المرور (قم بتشفير URL إذا لزم الأمر)
- **الحل:** انسخ الرابط مباشرة من لوحة تحكم Railway → تبويب Connect
- **مهم:** تأكد من أنك تستخدم **الرابط الداخلي** (`.railway.internal`)

### تحذير: رسوم Egress

- **السبب:** استخدام رابط Public TCP Proxy بكثرة
- **الحل:** تأكد من أن `DATABASE_URL` مضبوط على الرابط الداخلي (`.railway.internal`)
- **تحقق:** معظم العمليات يجب أن تستخدم `DATABASE_URL`، وليس `DIRECT_URL`

---

## نصائح تحسين التكلفة

1. ✅ **استخدم دائماً الرابط الداخلي لـ DATABASE_URL**
   - مجاني، سريع، لا رسوم egress

2. ✅ **اضبط DIRECT_URL لكن لا تقلق**
   - Prisma سيفضل `DATABASE_URL` لمعظم العمليات
   - يستخدم `DIRECT_URL` فقط عند الضرورة

3. ✅ **راقب استخدامك**
   - تحقق من لوحة تحكم Railway لاستخدام egress
   - إذا رأيت استخدام egress عالي، تحقق من أن `DATABASE_URL` داخلي

---

## ملخص سريع

### المتغيرات المطلوبة

1. **DATABASE_URL** - رابط داخلي خاص (`.railway.internal`) → **مجاني** ✅
2. **DIRECT_URL** - رابط Public TCP Proxy → يُستخدم فقط عند الحاجة
3. **NEXTAUTH_URL** - رابط تطبيقك العام
4. **NEXTAUTH_SECRET** - مفتاح سري مُولد

### لماذا هذا الحل هو الأفضل لك؟

✅ **توفير المال:** ستستخدم الشبكة الداخلية لـ Railway وهي مجانية تماماً

✅ **سرعة الموقع:** الاتصال الداخلي أسرع بكثير من الاتصال عبر الإنترنت العام

✅ **حل مشكلة البناء:** لن يحاول البرنامج الاتصال بالقاعدة أثناء "البناء" (وهو سبب الخطأ P1001 الذي ظهر لك)، بل سيتصل بها فقط عندما يفتح المستخدمون الموقع

---

**آخر تحديث:** 2025-01-01  
**المشروع:** health-poster-ai-platform  
**الإصدار:** 1.0.0
