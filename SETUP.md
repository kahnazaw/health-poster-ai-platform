# دليل الإعداد السريع

## خطوات التشغيل

### 1. تثبيت الحزم
```bash
npm install
```

### 2. إعداد قاعدة البيانات
```bash
# توليد Prisma Client
npm run db:generate

# إنشاء قاعدة البيانات
npm run db:push
```

### 3. إنشاء حساب المدير الافتراضي
```bash
npx ts-node scripts/init-db.ts
```

**بيانات تسجيل الدخول:**
- البريد: `admin@health.gov.iq`
- كلمة المرور: `Admin@123`

### 4. إنشاء ملف البيئة (اختياري)
```bash
cp .env.example .env.local
```

ثم عدّل `NEXTAUTH_SECRET` بقيمة عشوائية آمنة.

### 5. تشغيل المشروع
```bash
npm run dev
```

افتح المتصفح على: `http://localhost:3000`

## ملاحظات

- تأكد من تثبيت Node.js 18 أو أحدث
- في الإنتاج، استخدم قاعدة بيانات PostgreSQL أو MySQL
- غيّر كلمة مرور المدير بعد أول تسجيل دخول




