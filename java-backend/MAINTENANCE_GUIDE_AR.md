# دليل الصيانة والتشغيل - منصة بوسترات التوعية الصحية
## Maintenance & Operations Guide - Health Poster AI Platform

---

## 📋 نظرة عامة | Overview

هذا الدليل الشامل يشرح كيفية صيانة وإدارة المنصة بدون الحاجة لخبرة برمجية.

This comprehensive guide explains how to maintain and manage the platform without programming experience.

**الهدف:** جعل المنصة "موثقة ذاتياً" لسهولة الإدارة  
**Goal:** Make the platform "self-documented" for easy management

---

## 🏥 القسم 1: إدارة المراكز الصحية الـ 23
## Section 1: Managing the 23 Health Centers

### 1.1 إضافة مركز صحي جديد | Adding a New Health Center

#### الطريقة 1: من خلال قاعدة البيانات (SQL)

**الخطوات:**

1. **افتح قاعدة البيانات PostgreSQL**

2. **شغّل هذا الأمر:**
```sql
INSERT INTO user_profiles (
    id, 
    user_id, 
    health_center_name, 
    manager_name, 
    directorate_name, 
    logo_path, 
    show_verification_badge,
    posters_generated_count,
    created_at,
    updated_at
)
VALUES (
    gen_random_uuid()::text,
    'user_id_XX',  -- استبدل بمعرف المستخدم الفعلي
    'اسم المركز الصحي',
    'اسم مدير وحدة تعزيز الصحة',
    'دائرة صحة كركوك – قطاع كركوك الأول',
    'logo.jpg',
    true,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

**مثال:**
```sql
INSERT INTO user_profiles (
    id, user_id, health_center_name, manager_name, 
    directorate_name, logo_path, show_verification_badge,
    posters_generated_count, created_at, updated_at
)
VALUES (
    gen_random_uuid()::text,
    'hc_center_24',
    'المركز الصحي الرابع والعشرون',
    'د. محمد علي',
    'دائرة صحة كركوك – قطاع كركوك الأول',
    'logo.jpg',
    true,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

#### الطريقة 2: من خلال واجهة API

**استخدم هذا الأمر:**
```bash
curl -X POST http://localhost:8080/api/health-centers \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "hc_center_24",
    "healthCenterName": "المركز الصحي الرابع والعشرون",
    "managerName": "د. محمد علي"
  }'
```

---

### 1.2 تحديث بيانات مركز موجود | Updating Existing Center

#### من خلال قاعدة البيانات:

```sql
UPDATE user_profiles
SET 
    health_center_name = 'الاسم الجديد',
    manager_name = 'اسم المدير الجديد',
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = 'hc_center_01';
```

#### من خلال واجهة API:

```bash
curl -X PUT http://localhost:8080/api/health-centers/hc_center_01 \
  -H "Content-Type: application/json" \
  -d '{
    "healthCenterName": "الاسم الجديد",
    "managerName": "اسم المدير الجديد"
  }'
```

---

### 1.3 عرض بيانات مركز | Viewing Center Data

```bash
curl http://localhost:8080/api/health-centers/hc_center_01
```

---

### 1.4 عرض جميع المراكز | Viewing All Centers

```bash
curl http://localhost:8080/api/health-centers
```

---

## 🖼️ القسم 2: استبدال الشعار (logo.jpg)
## Section 2: Replacing the Logo (logo.jpg)

### 2.1 موقع الشعار | Logo Location

**المسار:**
```
java-backend/src/main/resources/static/assets/logos/logo.jpg
```

---

### 2.2 خطوات الاستبدال | Replacement Steps

#### الخطوة 1: إعداد الشعار الجديد

**المواصفات المطلوبة:**
- ✅ **التنسيق:** JPG أو PNG
- ✅ **الحجم الموصى به:** 600x600 بكسل (أو أكبر)
- ✅ **نسبة العرض إلى الارتفاع:** 1:1 (مربع)
- ✅ **الخلفية:** شفافة (PNG) أو بيضاء (JPG)

#### الخطوة 2: استبدال الملف

1. **احفظ الشعار الجديد** باسم `logo.jpg`
2. **انسخ الملف** إلى:
   ```
   java-backend/src/main/resources/static/assets/logos/logo.jpg
   ```
3. **استبدل الملف القديم** بالجديد

#### الخطوة 3: إعادة بناء التطبيق

```bash
cd java-backend
mvn clean install
```

#### الخطوة 4: إعادة التشغيل

```bash
mvn spring-boot:run
```

---

### 2.3 الحفاظ على جودة 300 DPI | Maintaining 300 DPI Quality

**مهم:** استبدال الشعار لا يؤثر على جودة البوسترات!

**Important:** Replacing the logo does not affect poster quality!

- ✅ البوسترات تبقى بجودة **300 DPI**
- ✅ الشعار يتم تغيير حجمه تلقائياً ليتناسب مع التخطيط
- ✅ الجودة مناسبة للطباعة في "AZAW TEAM CENTER"

**ملاحظة:** الشعار يتم تغيير حجمه إلى 150 بكسل ارتفاع مع الحفاظ على نسبة العرض إلى الارتفاع.

---

### 2.4 التحقق | Verification

**بعد الاستبدال، اختبر:**

```bash
# توليد بوستر تجريبي
curl -X POST http://localhost:8080/api/posters/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: test-user" \
  -d '{
    "topic": "نظافة الأسنان",
    "title": "اختبار الشعار",
    "bulletPoints": ["نقطة اختبار"],
    "language": "ar"
  }'
```

**تحقق من:**
- ✅ الشعار الجديد يظهر في أعلى البوستر
- ✅ الجودة واضحة
- ✅ الحجم مناسب

---

## 📊 القسم 3: التقرير الرسمي CSV
## Section 3: Official CSV Activity Report

### 3.1 الوصول للتقرير | Accessing the Report

#### من المتصفح:

```
http://localhost:8080/api/admin/statistics/export
```

**أو على Railway:**
```
https://your-app.railway.app/api/admin/statistics/export
```

#### باستخدام curl:

```bash
curl http://localhost:8080/api/admin/statistics/export \
  --output report.csv
```

---

### 3.2 محتوى التقرير | Report Content

**الرأس:**
- "Official Activity Report - Kirkuk Health Directorate - First Sector"
- "تقرير النشاط الرسمي - دائرة صحة كركوك – قطاع كركوك الأول"
- تاريخ ووقت التصدير

**الأعمدة:**
1. **Center ID** - معرف المركز
2. **Health Center Name** - اسم المركز الصحي
3. **Manager Name** - اسم المدير
4. **Total Posters Generated** - إجمالي البوسترات
5. **Last Activity Timestamp** - آخر نشاط

**الملخص:**
- إجمالي المراكز
- إجمالي البوسترات المولدة

---

### 3.3 فتح التقرير في Excel | Opening Report in Excel

**الخطوات:**

1. **حمّل الملف CSV** من الرابط
2. **افتح Excel**
3. **File → Open** → اختر الملف CSV
4. **سيتم عرض البيانات بشكل صحيح** (يدعم العربية)

**ملاحظة:** الملف يستخدم UTF-8 مع BOM، لذلك سيتم عرض العربية بشكل صحيح.

---

### 3.4 تفسير البيانات | Interpreting Data

**مثال صف:**
```
hc_center_01,المركز الصحي الأول,د. أحمد محمد علي,15,2025-01-15 14:25:00
```

**التفسير:**
- **Center ID:** `hc_center_01` - معرف المركز
- **Health Center Name:** `المركز الصحي الأول` - اسم المركز
- **Manager Name:** `د. أحمد محمد علي` - اسم المدير
- **Total Posters:** `15` - عدد البوسترات المولدة
- **Last Activity:** `2025-01-15 14:25:00` - آخر مرة تم فيها توليد بوستر

---

### 3.5 استخدام التقرير | Using the Report

**للعرض على المديرية:**

1. **حمّل التقرير** من الرابط
2. **افتحه في Excel**
3. **طباعته** أو **إرساله بالبريد الإلكتروني**
4. **التقرير جاهز للعرض** كما هو

**للتحليل:**

- **أكثر مركز نشاطاً:** ابحث عن أعلى عدد في عمود "Total Posters Generated"
- **أقل مركز نشاطاً:** ابحث عن أقل عدد
- **آخر نشاط:** راجع عمود "Last Activity Timestamp"

---

## 🚀 القسم 4: إدارة النشر على Railway
## Section 4: Managing Railway Deployment

### 4.1 إعادة تشغيل التطبيق | Restarting the Application

#### من لوحة تحكم Railway:

1. **افتح مشروعك** في Railway
2. **اذهب إلى Service** (الخدمة)
3. **اضغط على "Restart"** أو "Redeploy"
4. **انتظر حتى يكتمل** (2-5 دقائق)

#### من سطر الأوامر (CLI):

```bash
railway restart
```

---

### 4.2 إدارة متغيرات البيئة | Managing Environment Variables

#### إضافة متغير جديد:

1. **في لوحة تحكم Railway:**
   - اذهب إلى **Variables**
   - اضغط **+ New Variable**
   - أدخل **Name** و **Value**
   - احفظ

2. **المتغيرات المهمة:**
   - `DATABASE_URL` - رابط قاعدة البيانات (مطلوب)
   - `PORT` - المنفذ (يتم تعيينه تلقائياً)

#### تحديث متغير موجود:

1. **في Variables:**
   - ابحث عن المتغير
   - اضغط على **Edit**
   - غيّر القيمة
   - احفظ

#### حذف متغير:

1. **في Variables:**
   - ابحث عن المتغير
   - اضغط على **Delete**
   - أكد الحذف

---

### 4.3 عرض السجلات (Logs) | Viewing Logs

#### من لوحة تحكم Railway:

1. **افتح Service**
2. **اذهب إلى "Deployments"**
3. **اختر آخر deployment**
4. **اضغط على "View Logs"**

#### من سطر الأوامر:

```bash
railway logs
```

---

### 4.4 التحقق من الحالة | Checking Status

#### من المتصفح:

```
https://your-app.railway.app/api/templates/moh-topics
```

**إذا رأيت قائمة بالمواضيع:** ✅ التطبيق يعمل  
**إذا رأيت خطأ:** ❌ تحقق من Logs

---

### 4.5 تحديث الكود | Updating Code

#### الخطوات:

1. **عدّل الكود** محلياً
2. **ارفع التغييرات** إلى GitHub:
   ```bash
   git add .
   git commit -m "Update description"
   git push
   ```
3. **Railway سيبني تلقائياً** (5-10 دقائق)
4. **تحقق من النشر** في لوحة التحكم

---

## 🗄️ القسم 5: قاعدة البيانات
## Section 5: Database

### 5.1 تشغيل Migrations | Running Migrations

#### Migration V1 (إنشاء الجدول):

```sql
-- شغّل ملف: V1__create_user_profiles_table.sql
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

#### Migration V2 (إضافة العداد):

```sql
-- شغّل ملف: V2__add_posters_generated_count.sql
ALTER TABLE user_profiles 
ADD COLUMN IF NOT EXISTS posters_generated_count INTEGER DEFAULT 0;
```

---

### 5.2 نسخ احتياطي | Backup

#### إنشاء نسخة احتياطية:

```bash
pg_dump -h your-host -U your-user -d your-database > backup.sql
```

#### استعادة النسخة الاحتياطية:

```bash
psql -h your-host -U your-user -d your-database < backup.sql
```

---

### 5.3 استعلامات مفيدة | Useful Queries

#### عرض جميع المراكز:

```sql
SELECT 
    user_id,
    health_center_name,
    manager_name,
    posters_generated_count,
    updated_at
FROM user_profiles
ORDER BY posters_generated_count DESC;
```

#### البحث عن مركز:

```sql
SELECT * FROM user_profiles 
WHERE health_center_name LIKE '%اسم المركز%';
```

#### إعادة تعيين العداد:

```sql
UPDATE user_profiles 
SET posters_generated_count = 0 
WHERE user_id = 'hc_center_01';
```

---

## 🔧 القسم 6: استكشاف الأخطاء
## Section 6: Troubleshooting

### 6.1 الشعار لا يظهر | Logo Not Appearing

**الحل:**

1. **تحقق من الموقع:**
   ```
   java-backend/src/main/resources/static/assets/logos/logo.jpg
   ```

2. **تحقق من الاسم:** يجب أن يكون `logo.jpg` بالضبط

3. **أعد بناء التطبيق:**
   ```bash
   mvn clean install
   ```

4. **أعد التشغيل:**
   ```bash
   mvn spring-boot:run
   ```

---

### 6.2 خطأ في قاعدة البيانات | Database Error

**الحل:**

1. **تحقق من `DATABASE_URL`:**
   - يجب أن يبدأ بـ `jdbc:postgresql://`
   - تحقق من اسم المستخدم وكلمة المرور

2. **تحقق من الاتصال:**
   ```sql
   SELECT 1;
   ```

3. **تحقق من الجدول:**
   ```sql
   SELECT * FROM user_profiles LIMIT 1;
   ```

---

### 6.3 التطبيق لا يعمل على Railway | App Not Working on Railway

**الحل:**

1. **تحقق من Logs:**
   - اذهب إلى Railway → Deployments → View Logs

2. **تحقق من المتغيرات:**
   - تأكد من وجود `DATABASE_URL`

3. **تحقق من البناء:**
   - تأكد من أن البناء نجح (Build Succeeded)

4. **أعد النشر:**
   - اضغط على "Redeploy"

---

### 6.4 التقرير CSV فارغ | CSV Report Empty

**الحل:**

1. **تحقق من قاعدة البيانات:**
   ```sql
   SELECT COUNT(*) FROM user_profiles;
   ```

2. **تحقق من البيانات:**
   ```sql
   SELECT * FROM user_profiles;
   ```

3. **أعد المحاولة:**
   - انتظر قليلاً ثم حاول مرة أخرى

---

## 📚 القسم 7: المراجع السريعة
## Section 7: Quick References

### 7.1 روابط مهمة | Important Links

**محلياً:**
- عرض الإحصائيات: `http://localhost:8080/api/admin/statistics`
- تحميل CSV: `http://localhost:8080/api/admin/statistics/export`
- توليد بوستر: `POST http://localhost:8080/api/posters/generate`
- بوستر ترحيبي: `GET http://localhost:8080/api/posters/welcome/{centerId}`

**على Railway:**
- استبدل `localhost:8080` بـ رابط Railway الخاص بك

---

### 7.2 أوامر مفيدة | Useful Commands

```bash
# بناء التطبيق
mvn clean install

# تشغيل محلياً
mvn spring-boot:run

# عرض الإحصائيات
curl http://localhost:8080/api/admin/statistics

# تحميل CSV
curl http://localhost:8080/api/admin/statistics/export --output report.csv
```

---

### 7.3 هيكل الملفات | File Structure

```
java-backend/
├── src/main/
│   ├── java/.../          # الكود البرمجي
│   └── resources/
│       ├── static/assets/logos/  # الشعارات
│       └── db/migration/          # ملفات SQL
├── pom.xml                # التبعيات
├── Dockerfile             # إعدادات Docker
├── nixpacks.toml          # إعدادات Railway
└── railway.json           # إعدادات Railway
```

---

## ✅ القسم 8: قائمة التحقق
## Section 8: Checklist

### قبل النشر | Before Deployment

- [ ] قاعدة البيانات جاهزة
- [ ] `DATABASE_URL` مضبوط في Railway
- [ ] `logo.jpg` موجود في `static/assets/logos/`
- [ ] جميع المراكز الـ 23 موجودة في قاعدة البيانات
- [ ] تم اختبار توليد بوستر محلياً

### بعد النشر | After Deployment

- [ ] التطبيق يعمل (تحقق من `/api/templates/moh-topics`)
- [ ] الشعار يظهر على البوسترات
- [ ] التذييل يعرض اسم المركز والمدير
- [ ] التقرير CSV يعمل
- [ ] الإحصائيات دقيقة

---

## 🆘 الدعم | Support

### للمساعدة:

1. **راجع السجلات (Logs):**
   - Railway → Deployments → View Logs

2. **راجع التوثيق:**
   - `README.md` - دليل المشروع
   - `QUALITY_AUDIT_REPORT.md` - تقرير الجودة
   - `STATISTICS_GUIDE.md` - دليل الإحصائيات

3. **تحقق من قاعدة البيانات:**
   - تأكد من أن الجداول موجودة
   - تأكد من وجود البيانات

---

## 📝 ملاحظات مهمة | Important Notes

1. **الشعار:** يجب أن يكون اسمه `logo.jpg` بالضبط
2. **قاعدة البيانات:** يجب أن يكون `DATABASE_URL` مضبوطاً
3. **المراكز:** كل مركز يحتاج `user_id` فريد
4. **الجودة:** جميع البوسترات بجودة 300 DPI دائماً
5. **التتبع:** يتم تلقائياً - لا حاجة لإجراء يدوي

---

## ✨ الخلاصة | Summary

هذا الدليل يحتوي على كل ما تحتاجه لإدارة المنصة بسهولة.

This guide contains everything you need to manage the platform easily.

**المنصة موثقة ذاتياً وجاهزة للإدارة!**

**Platform is self-documented and ready for management!**

---

**تم التطوير لدائرة صحة كركوك – قطاع كركوك الأول**  
**Developed for Kirkuk Health Directorate - First Sector**

**آخر تحديث:** 2025-01-XX  
**Last Updated:** 2025-01-XX

