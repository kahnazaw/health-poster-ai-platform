# دليل الإعداد والتكوين

## نظرة عامة

هذا الدليل يشرح كيفية إعداد وتشغيل منصة بوسترات التوعية الصحية من الصفر.

## المتطلبات الأساسية

- **Node.js** 18.x أو أحدث
- **npm** أو **yarn**
- **PostgreSQL** (للإنتاج) أو **SQLite** (للتطوير المحلي)
- **Git**

## خطوات الإعداد

### 1. تثبيت الحزم

```bash
npm install
```

### 2. إعداد قاعدة البيانات

#### للتطوير المحلي (SQLite - اختياري)

```bash
# تعديل prisma/schema.prisma لاستخدام sqlite
# ثم:
npm run db:generate
npm run db:push
```

#### للإنتاج (PostgreSQL)

```bash
# تأكد من وجود DATABASE_URL في .env.local
npm run db:generate
npm run db:migrate
```

### 3. إعداد متغيرات البيئة

أنشئ ملف `.env.local`:

```env
# Database
DATABASE_URL="postgresql://user:password@localhost:5432/health_poster"

# NextAuth Configuration
NEXTAUTH_URL="http://localhost:3000"
NEXTAUTH_SECRET="generate-a-random-secret-here"

# Admin User (Auto-created in production)
ADMIN_EMAIL="admin@health.gov.iq"
ADMIN_PASSWORD="ChangeThisPassword123!"

# OpenAI API (Optional - for AI content generation)
OPENAI_API_KEY="sk-your-openai-api-key"
```

**ملاحظة:** استخدم `openssl rand -base64 32` لتوليد `NEXTAUTH_SECRET` آمن.

### 4. تشغيل المشروع

```bash
# وضع التطوير
npm run dev

# بناء للإنتاج
npm run build
npm start
```

افتح المتصفح على: `http://localhost:3000`

## نظام المصادقة والصلاحيات

### الأدوار والصلاحيات

النظام يستخدم نظام RBAC (Role-Based Access Control) مركزي في `lib/permissions.ts`.

#### SUPER_ADMIN (مدير عام)
- جميع الصلاحيات
- إدارة المنظمات
- عرض جميع التحليلات

#### ADMIN (مدير)
- إدارة المستخدمين (إضافة، تعديل، حذف، تغيير الصلاحيات)
- إدارة جميع البوسترات
- مراجعة وموافقة المحتوى
- إنشاء وإدارة القوالب
- عرض التحليلات

#### CONTENT_MANAGER (مدير المحتوى)
- إنشاء وتعديل جميع البوسترات
- إنشاء وإدارة القوالب
- مراجعة وموافقة المحتوى
- عرض التحليلات الأساسية

#### REVIEWER (مراجع)
- مراجعة وموافقة/رفض البوسترات
- إضافة تعليقات المراجعة
- إنشاء البوسترات الخاصة

#### USER (مستخدم)
- توليد البوسترات
- عرض وإدارة بوستراته الخاصة
- استخدام القوالب العامة

### دورة حياة المحتوى

1. **DRAFT**: البوستر في حالة المسودة
2. **UNDER_REVIEW**: قيد المراجعة
3. **APPROVED**: معتمد ويمكن نشره
4. **REJECTED**: مرفوض (مع سبب الرفض)

### نظام المراجعة

- المراجعون يمكنهم إضافة تعليقات
- كل تغيير في الحالة يُسجل في StatusHistory
- فقط المحتوى المعتمد يمكن تصديره (ما لم يكن المستخدم ADMIN)

### إنشاء حساب المدير

#### في الإنتاج
يتم إنشاء حساب المدير تلقائياً عند بدء التشغيل من متغيرات البيئة:
- `ADMIN_EMAIL`
- `ADMIN_PASSWORD`

#### في التطوير
يمكن استخدام سكربت `scripts/init-db.ts` أو إنشاء المستخدم يدوياً.

## قاعدة البيانات

### المخطط

#### User Model
```prisma
model User {
  id        String   @id @default(cuid())
  email     String   @unique
  password  String   // Hashed with bcrypt
  name      String
  role      String   @default("USER") // "ADMIN" | "USER"
  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt
  posters   Poster[]
}
```

#### Poster Model
```prisma
model Poster {
  id          String   @id @default(cuid())
  title       String
  topic       String
  content     String   // JSON string
  imageUrl    String?
  userId      String
  user        User     @relation(fields: [userId], references: [id])
  createdAt   DateTime @default(now())
  updatedAt   DateTime @updatedAt
}
```

### Migrations

```bash
# إنشاء migration جديد
npm run db:migrate

# تطبيق migrations في الإنتاج
npm run db:migrate:deploy
```

## التصدير متعدد القنوات

### صيغ التصدير المتاحة

- **PNG عالي الجودة**: للطباعة والاستخدام الرقمي
- **PDF A4/A3**: جاهز للطباعة
- **Instagram**: صيغة مربعة (1080x1080)
- **WhatsApp**: صيغة عمودية (1080x1920)
- **Facebook**: صيغة أفقية (1200x630)

### QR Code

كل بوستر معتمد يحصل على:
- QR Code فريد
- رابط عام للمشاركة (`/public/posters/[publicId]`)
- يمكن تتبع الوصول

### قواعد التصدير

- البوسترات المعتمدة فقط يمكن تصديرها (ما لم يكن المستخدم ADMIN)
- يتم تتبع كل عملية تحميل/طباعة
- QR Code يربط بصفحة العرض العامة

## دعم متعدد اللغات (i18n)

### اللغات المدعومة

- **العربية** (افتراضي)
- **الكردية**
- **التركية**
- **الإنجليزية**

### التكوين

الترجمة موجودة في `lib/i18n.ts`:
- ترجمة واجهة المستخدم
- ترجمة رسائل النظام
- دعم ترجمة محتوى البوسترات

### استخدام AI للترجمة

يمكن استخدام OpenAI لترجمة المحتوى:
- زر "ترجمة بالذكاء الاصطناعي" في واجهة التوليد
- الترجمة قابلة للتعديل اليدوي
- Fallback للترجمة اليدوية

### تفضيلات اللغة

- يتم حفظ تفضيل اللغة لكل مستخدم
- يمكن تغيير اللغة من الإعدادات

## إدارة المنظمات

### إنشاء منظمة

1. تسجيل الدخول كـ SUPER_ADMIN أو ADMIN
2. الانتقال إلى `/dashboard/admin/organizations`
3. النقر على "إضافة منظمة جديدة"
4. إدخال:
   - الاسم
   - الرمز (فريد)
   - الوصف (اختياري)

### إدارة المستخدمين

- يمكن تعيين المستخدمين لمنظمات
- كل منظمة لها مستخدميها وقوالبها
- القوالب العامة (`isPublic: true`) متاحة لجميع المنظمات

### عزل البيانات

- المستخدمون يرون فقط محتوى منظمتهم
- ADMINs يمكنهم عرض جميع المنظمات
- SUPER_ADMIN لديه صلاحيات كاملة

## لوحة التحليلات

### الوصول

- `/dashboard/admin/analytics`
- متاحة لـ: ADMIN, SUPER_ADMIN, CONTENT_MANAGER

### البيانات المعروضة

- **نظرة عامة:**
  - إجمالي البوسترات
  - البوسترات المعتمدة
  - التحميلات والطباعة
  - استخدام الذكاء الاصطناعي
  - عدد المستخدمين والقوالب

- **الرسوم البيانية:**
  - توزيع البوسترات حسب الحالة (Pie Chart)
  - البوسترات حسب الحالة (Bar Chart)
  - إحصائيات إضافية

### الخصوصية

- البيانات مجمعة فقط
- لا يتم تتبع بيانات شخصية
- لا يتم تسجيل معلومات المستخدمين الفردية

## تكامل الذكاء الاصطناعي

### إعداد OpenAI

1. **الحصول على API Key:**
   - سجل في [OpenAI Platform](https://platform.openai.com/)
   - أنشئ API Key جديد
   - أضفه إلى `.env.local` كـ `OPENAI_API_KEY`

2. **الاستخدام:**
   - في واجهة توليد البوستر، فعّل خيار "استخدام الذكاء الاصطناعي"
   - اختر الجمهور المستهدف، الأسلوب، والطول
   - سيتم استخدام OpenAI لتوليد محتوى مخصص

### بدون OpenAI

إذا لم يتم إعداد `OPENAI_API_KEY`:
- سيتم استخدام القوالب الجاهزة تلقائياً
- التطبيق يعمل بشكل كامل بدون OpenAI

### بنية خدمة AI

الخدمة موجودة في `lib/ai.ts`:
- Provider-agnostic (يمكن استبدال OpenAI بمزود آخر)
- Fallback تلقائي للقوالب
- معالجة أخطاء آمنة

## النشر على Railway

### المتطلبات
- حساب Railway
- مستودع GitHub متصل

### خطوات النشر

1. **إضافة PostgreSQL:**
   - في Railway Dashboard، أضف خدمة PostgreSQL
   - Railway سينشئ `DATABASE_URL` تلقائياً

2. **إضافة متغيرات البيئة:**
   ```
   NEXTAUTH_URL=https://your-app.railway.app
   NEXTAUTH_SECRET=your-secret-here
   ADMIN_EMAIL=admin@health.gov.iq
   ADMIN_PASSWORD=SecurePassword123!
   OPENAI_API_KEY=sk-... (اختياري)
   ```

3. **النشر:**
   - Railway سيكتشف `nixpacks.toml` تلقائياً
   - سيقوم بـ:
     - تثبيت Node.js 18
     - تثبيت الحزم
     - توليد Prisma Client
     - بناء Next.js
     - تطبيق Migrations عند البدء
     - تشغيل التطبيق

### Nixpacks Configuration

المشروع يستخدم `nixpacks.toml`:
```toml
[phases.setup]
nixPkgs = ["nodejs_18"]

[phases.build]
cmds = [
  "npm install",
  "npx prisma generate",
  "npm run build"
]

[start]
cmd = "npx prisma migrate deploy && npm run start"
```

## سير العمل الإداري

### إضافة مستخدم جديد

1. تسجيل الدخول كـ Admin
2. الانتقال إلى `/dashboard/admin`
3. النقر على "إضافة مستخدم جديد"
4. ملء البيانات:
   - الاسم
   - البريد الإلكتروني
   - كلمة المرور
   - الصلاحية (USER أو ADMIN)

### تغيير صلاحيات مستخدم

1. في لوحة التحكم الإدارية
2. النقر على "تعديل" بجانب المستخدم
3. تغيير الصلاحية من القائمة المنسدلة
4. حفظ التغييرات

### توليد بوستر

1. تسجيل الدخول
2. الانتقال إلى `/dashboard/user`
3. النقر على "إنشاء بوستر جديد"
4. إدخال موضوع التوعية الصحية
5. (اختياري) تفعيل الذكاء الاصطناعي واختيار الخيارات
6. النقر على "توليد البوستر"
7. تحميل أو طباعة البوستر

## استكشاف الأخطاء

### خطأ في قاعدة البيانات

```bash
# التحقق من الاتصال
npx prisma db pull

# إعادة توليد Prisma Client
npm run db:generate
```

### خطأ في NextAuth

- تأكد من وجود `NEXTAUTH_SECRET` و `NEXTAUTH_URL`
- تأكد من أن `NEXTAUTH_URL` يطابق رابط التطبيق

### خطأ في AI Generation

- تأكد من صحة `OPENAI_API_KEY`
- تحقق من رصيد OpenAI API
- التطبيق سيعمل بدون OpenAI (سيستخدم القوالب)

### خطأ في البناء على Railway

- تأكد من وجود `nixpacks.toml`
- تحقق من متغيرات البيئة
- راجع سجلات Railway للتفاصيل

## الأمان

### أفضل الممارسات

1. **كلمات المرور:**
   - استخدم كلمات مرور قوية
   - غيّر كلمة مرور المدير الافتراضية

2. **متغيرات البيئة:**
   - لا ترفع `.env.local` إلى Git
   - استخدم قيم آمنة لـ `NEXTAUTH_SECRET`

3. **قاعدة البيانات:**
   - استخدم اتصال آمن (SSL) في الإنتاج
   - قم بعمل نسخ احتياطي منتظم

4. **API Keys:**
   - لا تشارك API Keys
   - استخدم متغيرات البيئة فقط

## الدعم

للأسئلة أو المشاكل:
1. راجع سجلات التطبيق
2. تحقق من متغيرات البيئة
3. راجع هذا الدليل
4. تواصل مع فريق التطوير
