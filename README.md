# منصة بوسترات التوعية الصحية

منصة مؤسسية رسمية لتوليد بوسترات التوعية الصحية بالذكاء الاصطناعي

**الجهة المالكة:** دائرة صحة كركوك – قطاع كركوك الأول – وحدة تعزيز الصحة

## المميزات

### الأمان والصلاحيات
- ✅ نظام تسجيل دخول آمن مع NextAuth.js
- ✅ صلاحيات متعددة المستويات (Admin / User)
- ✅ حماية المسارات حسب الدور
- ✅ إنشاء تلقائي لحساب المدير عند بدء التشغيل

### توليد البوسترات
- ✅ توليد محتوى توعية صحية احترافي
- ✅ **دعم الذكاء الاصطناعي (OpenAI)** لتوليد محتوى ذكي ومخصص
- ✅ قوالب جاهزة للمواضيع الشائعة
- ✅ تحميل الصور عالية الجودة (PNG)
- ✅ تحميل PDF جاهز للطباعة
- ✅ طباعة مباشرة
- ✅ معاينة مباشرة قبل الحفظ

### واجهة المستخدم
- ✅ تصميم عصري ومؤسسي بألوان طبية هادئة
- ✅ دعم RTL كامل للغة العربية
- ✅ متجاوب مع جميع الأجهزة (Desktop / Mobile)
- ✅ لوحة تحكم إدارية لإدارة المستخدمين
- ✅ لوحة تحكم للمستخدمين لعرض وإدارة بوستراتهم

## التقنيات المستخدمة

### Frontend
- **Next.js 14** (App Router) - إطار عمل React
- **TypeScript** - للسلامة النوعية
- **Tailwind CSS** - للتصميم
- **NextAuth.js** - للمصادقة

### Backend
- **Next.js API Routes** - واجهات برمجية
- **Prisma ORM** - إدارة قاعدة البيانات
- **PostgreSQL** - قاعدة البيانات
- **bcryptjs** - تشفير كلمات المرور

### AI & Export
- **OpenAI API** (اختياري) - توليد المحتوى بالذكاء الاصطناعي
- **html2canvas** - تحويل HTML إلى صور
- **jsPDF** - توليد ملفات PDF

## التثبيت والتشغيل

### المتطلبات
- Node.js 18 أو أحدث
- PostgreSQL (للإنتاج) أو SQLite (للتطوير)
- npm أو yarn

### 1. تثبيت الحزم

```bash
npm install
```

### 2. إعداد متغيرات البيئة

أنشئ ملف `.env.local`:

```env
# Database
DATABASE_URL="postgresql://user:password@localhost:5432/health_poster"

# NextAuth
NEXTAUTH_URL="http://localhost:3000"
NEXTAUTH_SECRET="your-secret-key-here"

# Admin User (Auto-created on startup in production)
ADMIN_EMAIL="admin@health.gov.iq"
ADMIN_PASSWORD="SecurePassword123!"

# OpenAI (Optional - for AI content generation)
OPENAI_API_KEY="sk-..."
```

### 3. إعداد قاعدة البيانات

```bash
# توليد Prisma Client
npm run db:generate

# تطبيق Migrations
npm run db:migrate

# أو للبيئة المحلية
npm run db:push
```

### 4. تشغيل المشروع

```bash
# وضع التطوير
npm run dev

# بناء للإنتاج
npm run build
npm start
```

المشروع سيعمل على: `http://localhost:3000`

## هيكل المشروع

```
health-poster-ai-platform/
├── app/                    # صفحات Next.js (App Router)
│   ├── api/               # API Routes
│   │   ├── auth/          # NextAuth endpoints
│   │   ├── users/         # إدارة المستخدمين
│   │   └── posters/       # إدارة البوسترات
│   ├── dashboard/         # لوحات التحكم
│   │   ├── admin/        # لوحة التحكم الإدارية
│   │   └── user/         # لوحة تحكم المستخدم
│   ├── login/             # صفحة تسجيل الدخول
│   ├── posters/           # صفحات البوسترات
│   └── layout.tsx         # التخطيط الرئيسي
├── components/            # المكونات القابلة لإعادة الاستخدام
│   ├── Navbar.tsx        # شريط التنقل
│   ├── PosterGenerator.tsx # مولد البوسترات
│   └── UserManagement.tsx  # إدارة المستخدمين
├── lib/                   # المكتبات والمساعدات
│   ├── auth.ts           # إعدادات NextAuth
│   ├── prisma.ts         # Prisma Client
│   └── ai.ts             # خدمة الذكاء الاصطناعي
├── prisma/                # مخطط قاعدة البيانات
│   ├── schema.prisma     # مخطط Prisma
│   └── migrations/       # Migrations
├── scripts/               # سكربتات مساعدة
│   ├── ensure-admin.ts   # إنشاء المدير تلقائياً
│   └── init-db.ts        # تهيئة قاعدة البيانات
└── types/                 # تعريفات TypeScript
```

## الصلاحيات

### Admin (مدير)
- إضافة مستخدمين جدد
- تعديل بيانات المستخدمين
- تغيير صلاحيات المستخدمين (USER ↔ ADMIN)
- حذف المستخدمين
- توليد البوسترات
- عرض جميع البوسترات

### User (مستخدم)
- توليد البوسترات
- عرض وإدارة بوستراته الخاصة
- تحميل وطباعة البوسترات

## استخدام API

### توليد بوستر

```bash
POST /api/posters/generate
Content-Type: application/json

{
  "topic": "نظافة الأسنان",
  "useAI": true,              # اختياري - استخدام الذكاء الاصطناعي
  "targetAudience": "عامة",   # اختياري - الجمهور المستهدف
  "tone": "formal",          # اختياري - formal | friendly
  "length": "medium"         # اختياري - short | medium | long
}
```

### إدارة المستخدمين (Admin فقط)

```bash
# الحصول على جميع المستخدمين
GET /api/users

# إضافة مستخدم جديد
POST /api/users
{
  "email": "user@example.com",
  "password": "password123",
  "name": "اسم المستخدم",
  "role": "USER"
}

# تعديل مستخدم
PATCH /api/users/[id]
{
  "name": "اسم جديد",
  "role": "ADMIN"
}

# حذف مستخدم
DELETE /api/users/[id]
```

## الذكاء الاصطناعي

### إعداد OpenAI

1. احصل على API Key من [OpenAI](https://platform.openai.com/)
2. أضف `OPENAI_API_KEY` إلى متغيرات البيئة
3. عند تفعيل خيار "استخدام الذكاء الاصطناعي" في واجهة التوليد، سيتم استخدام OpenAI لتوليد المحتوى

### بدون OpenAI

إذا لم يتم إعداد OpenAI API Key، سيتم استخدام القوالب الجاهزة تلقائياً.

## النشر على Railway

### المتطلبات
- حساب Railway
- قاعدة بيانات PostgreSQL (Railway يوفرها)
- متغيرات البيئة المطلوبة

### خطوات النشر

1. اربط مستودع GitHub مع Railway
2. أضف متغيرات البيئة:
   - `DATABASE_URL` (يتم إنشاؤه تلقائياً عند إضافة PostgreSQL)
   - `NEXTAUTH_URL` (رابط التطبيق على Railway)
   - `NEXTAUTH_SECRET` (مفتاح عشوائي آمن)
   - `ADMIN_EMAIL` و `ADMIN_PASSWORD`
   - `OPENAI_API_KEY` (اختياري)

3. Railway سيقوم تلقائياً بـ:
   - تثبيت الحزم
   - توليد Prisma Client
   - تطبيق Migrations
   - تشغيل التطبيق

## الأمان

- كلمات المرور مشفرة باستخدام bcrypt
- جلسات آمنة مع NextAuth.js
- حماية CSRF مدمجة
- لا يتم تسجيل بيانات حساسة في السجلات
- Prisma Client يستخدم Binary Engine متوافق مع Railway

## ملاحظات مهمة

1. **الأمان:** تأكد من تغيير `NEXTAUTH_SECRET` بقيمة عشوائية آمنة
2. **قاعدة البيانات:** يستخدم PostgreSQL في الإنتاج
3. **الذكاء الاصطناعي:** اختياري - يعمل التطبيق بدون OpenAI
4. **الطباعة:** البوسترات مصممة بحجم A4 عمودي
5. **المدير التلقائي:** يتم إنشاء حساب المدير تلقائياً في الإنتاج من متغيرات البيئة

## الترخيص

هذا المشروع مملوك لدائرة صحة كركوك – قطاع كركوك الأول – وحدة تعزيز الصحة

## الدعم

للأسئلة والدعم الفني، يرجى التواصل مع فريق التطوير.
