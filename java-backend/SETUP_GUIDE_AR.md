# دليل الإعداد الكامل - منصة بوسترات التوعية الصحية
## Complete Setup Guide - Health Poster AI Platform

---

## 🎯 نظرة عامة | Overview

هذا الدليل يشرح كيفية إعداد المنصة بالكامل من الصفر، حتى لو لم يكن لديك خبرة برمجية.

This guide explains how to set up the platform completely from scratch, even if you have no programming experience.

---

## 📋 المتطلبات | Requirements

### ما تحتاجه | What You Need:

1. **حاسوب** يعمل بنظام Windows أو Mac أو Linux
2. **اتصال بالإنترنت**
3. **حساب على Railway** (مجاني للبداية)
4. **ملف الشعار** `logo.jpg` (شعار دائرة صحة كركوك)

---

## 🚀 الخطوة 1: إعداد الملفات | Step 1: Setup Files

### 1.1 إضافة الشعار | Add Logo

**ضع ملف الشعار في هذا المكان:**
```
java-backend/src/main/resources/static/assets/logos/logo.jpg
```

**كيفية القيام بذلك:**
1. افتح مجلد المشروع
2. اذهب إلى: `java-backend` → `src` → `main` → `resources` → `static` → `assets` → `logos`
3. انسخ ملف `logo.jpg` إلى هذا المجلد

---

## 🗄️ الخطوة 2: إعداد قاعدة البيانات | Step 2: Database Setup

### 2.1 إنشاء الجدول | Create Table

افتح قاعدة البيانات PostgreSQL وقم بتشغيل هذا الأمر:

```sql
CREATE TABLE IF NOT EXISTS user_profiles (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    health_center_name VARCHAR(200),
    manager_name VARCHAR(150),
    directorate_name VARCHAR(200) DEFAULT 'دائرة صحة كركوك – قطاع كركوك الأول',
    logo_path VARCHAR(500) DEFAULT 'logo.jpg',
    show_verification_badge BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.2 إضافة بيانات المراكز الصحية الـ 23 | Add 23 Health Centers

قم بتشغيل ملف: `src/main/resources/db/seed/seed_23_health_centers.sql`

**ملاحظة مهمة:** استبدل `user_id_01` إلى `user_id_23` بمعرفات المستخدمين الفعلية من جدول المستخدمين.

---

## ⚙️ الخطوة 3: إعداد المتغيرات | Step 3: Environment Variables

### 3.1 الملفات المحلية | Local Files

افتح: `java-backend/src/main/resources/application.properties`

وحدّث هذه الأسطر:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/health_poster_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3.2 على Railway | On Railway

في لوحة تحكم Railway، أضف هذه المتغيرات:

- `DATABASE_URL`: رابط قاعدة البيانات من Railway
- `PORT`: سيتم تعيينه تلقائياً

---

## 🏗️ الخطوة 4: البناء والتشغيل | Step 4: Build and Run

### 4.1 البناء | Build

افتح Terminal (سطر الأوامر) واكتب:

```bash
cd java-backend
mvn clean install
```

**انتظر حتى ينتهي البناء** (قد يستغرق 2-5 دقائق)

### 4.2 التشغيل | Run

```bash
mvn spring-boot:run
```

**سترى رسالة:** `Started HealthPosterAiPlatformApplication`

---

## 🧪 الخطوة 5: الاختبار | Step 5: Testing

### 5.1 اختبار API | Test API

افتح متصفح الإنترنت واذهب إلى:

```
http://localhost:8080/api/templates/moh-topics
```

**يجب أن ترى قائمة بالمواضيع المعتمدة من وزارة الصحة**

### 5.2 توليد بوستر تجريبي | Generate Test Poster

استخدم هذا الأمر (في Terminal جديد):

```bash
curl -X POST http://localhost:8080/api/posters/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: test-user" \
  -d '{
    "topic": "نظافة الأسنان",
    "title": "نظافة الأسنان: أساس الصحة الفموية",
    "bulletPoints": [
      "اغسل أسنانك مرتين يومياً",
      "استخدم خيط الأسنان يومياً"
    ],
    "language": "ar",
    "useMOHGuidelines": true
  }'
```

**ستحصل على صورة البوستر** مع:
- ✅ شعار دائرة صحة كركوك في الأعلى
- ✅ التذييل مع اسم المركز واسم المدير
- ✅ جودة 300 DPI للطباعة

---

## 🚢 الخطوة 6: النشر على Railway | Step 6: Deploy to Railway

### 6.1 رفع الكود | Upload Code

1. ارفع الكود إلى GitHub
2. في Railway، اختر "New Project"
3. اختر "Deploy from GitHub repo"
4. اختر المستودع الخاص بك

### 6.2 إعداد المتغيرات | Set Variables

في Railway:
1. اذهب إلى "Variables"
2. أضف `DATABASE_URL` مع رابط قاعدة البيانات
3. احفظ

### 6.3 الانتظار | Wait

Railway سيبني التطبيق تلقائياً (5-10 دقائق)

---

## ✅ التحقق | Verification

### بعد النشر، تحقق من:

1. ✅ التطبيق يعمل: `https://your-app.railway.app/api/templates/moh-topics`
2. ✅ الشعار يظهر على البوسترات
3. ✅ التذييل يعرض اسم المركز والمدير
4. ✅ الصور بجودة 300 DPI

---

## 🆘 حل المشاكل | Troubleshooting

### المشكلة: الشعار لا يظهر
**الحل:**
- تأكد من وجود `logo.jpg` في `static/assets/logos/`
- أعد بناء التطبيق: `mvn clean install`

### المشكلة: خطأ في قاعدة البيانات
**الحل:**
- تحقق من `DATABASE_URL`
- تأكد من إنشاء جدول `user_profiles`

### المشكلة: التطبيق لا يعمل على Railway
**الحل:**
- تحقق من Logs في Railway
- تأكد من إعداد `DATABASE_URL` بشكل صحيح

---

## 📞 الدعم | Support

إذا واجهت أي مشكلة، راجع:
- `README.md` - دليل المشروع
- `QUICK_START.md` - دليل البدء السريع
- `ARCHITECTURE.md` - بنية النظام

---

## 🎉 تهانينا! | Congratulations!

المنصة جاهزة الآن لتوليد بوسترات احترافية بجودة طباعة!

The platform is now ready to generate professional print-quality posters!

---

**تم التطوير لدائرة صحة كركوك – قطاع كركوك الأول**  
**Developed for Kirkuk Health Directorate - First Sector**

