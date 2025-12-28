# Welcome Poster Generator - دليل بوستر الترحيب
## Guide for Welcome Poster Feature

---

## 🎯 نظرة عامة | Overview

تم إضافة ميزة جديدة: **Welcome Poster Generator** (مولد بوستر الترحيب)

A new feature has been added: **Welcome Poster Generator**

هذه الميزة تسمح لكل مركز صحي بإنشاء بوستر ترحيبي تلقائياً يعرض:
- شعار دائرة صحة كركوك (logo.jpg)
- رسالة ترحيب بالعربية والتركمانية
- اسم المركز الصحي واسم المدير من قاعدة البيانات

This feature allows each health center to automatically generate a welcome poster displaying:
- Kirkuk Health Directorate logo (logo.jpg)
- Welcome message in Arabic and Turkmen
- Health Center Name and Manager Name from database

---

## 🌐 نقطة النهاية | Endpoint

### GET `/api/posters/welcome/{centerId}`

**الوصف:** توليد بوستر ترحيبي للمركز الصحي  
**Description:** Generate welcome poster for health center

**المعاملات:**  
**Parameters:**
- `centerId` (Path Variable) - معرف المركز الصحي (هو نفسه user_id في جدول user_profiles)

**الاستجابة:**  
**Response:**
- صورة PNG مباشرة (300 DPI)
- Direct PNG image (300 DPI)

---

## 📋 كيفية الاستخدام | How to Use

### مثال 1: من المتصفح | Example 1: From Browser

افتح الرابط في المتصفح:
```
http://localhost:8080/api/posters/welcome/hc_center_01
```

**النتيجة:** ستظهر الصورة مباشرة في المتصفح

### مثال 2: باستخدام curl | Example 2: Using curl

```bash
curl -X GET http://localhost:8080/api/posters/welcome/hc_center_01 \
  --output welcome-poster.png
```

### مثال 3: في HTML | Example 3: In HTML

```html
<img src="http://localhost:8080/api/posters/welcome/hc_center_01" 
     alt="Welcome Poster" />
```

---

## 🎨 محتوى البوستر | Poster Content

### الرأس (Header):
- ✅ شعار دائرة صحة كركوك (logo.jpg)
- ✅ اسم المديرية: "دائرة صحة كركوك – قطاع كركوك الأول"

### المحتوى (Body):
**بالعربية:**
- "مرحباً بكم في منصة تعزيز الصحة بالذكاء الاصطناعي"
- "معاً من أجل كركوك أكثر صحة"

**بالتركمانية:**
- "Sağlığı Geliştirme Yapay Zeka Platformuna Hoş Geldiniz"
- "Kerkük'ün sağlığı için birlikteyiz"

### التذييل (Footer):
- ✅ اسم المركز الصحي (من قاعدة البيانات)
- ✅ اسم مدير وحدة تعزيز الصحة (من قاعدة البيانات)

---

## 🔧 المتطلبات | Requirements

### يجب أن يكون المركز موجوداً في قاعدة البيانات:

```sql
SELECT * FROM user_profiles WHERE user_id = 'hc_center_01';
```

**يجب أن يحتوي على:**
- `health_center_name` - اسم المركز الصحي
- `manager_name` - اسم المدير

---

## ✅ التحقق | Verification

### 1. تحقق من وجود المركز:

```bash
curl http://localhost:8080/api/health-centers/hc_center_01
```

### 2. توليد بوستر الترحيب:

```bash
curl -X GET http://localhost:8080/api/posters/welcome/hc_center_01 \
  --output test-welcome.png
```

### 3. فتح الصورة:

افتح `test-welcome.png` وتحقق من:
- ✅ الشعار في الأعلى
- ✅ رسالة الترحيب في الوسط
- ✅ اسم المركز والمدير في الأسفل
- ✅ جودة 300 DPI

---

## 🖨️ جودة الطباعة | Print Quality

- **DPI:** 300 (جودة احترافية)
- **الحجم:** A4 (2480x3508 بكسل)
- **التنسيق:** PNG
- **مناسب للطباعة في:** "AZAW TEAM CENTER"

---

## 📝 أمثلة الاستخدام | Usage Examples

### للمراكز الـ 23:

```bash
# المركز الأول
http://localhost:8080/api/posters/welcome/hc_center_01

# المركز الثاني
http://localhost:8080/api/posters/welcome/hc_center_02

# ... إلخ
```

### في التطبيق:

```javascript
// JavaScript example
const centerId = 'hc_center_01';
const welcomePosterUrl = `http://localhost:8080/api/posters/welcome/${centerId}`;

// Display in img tag
document.getElementById('welcome-poster').src = welcomePosterUrl;
```

---

## 🎯 حالات الاستخدام | Use Cases

1. **صفحة ترحيب:** عرض البوستر في صفحة ترحيب للمدير
2. **شهادة تذكارية:** طباعة البوستر كشهادة تذكارية
3. **رابط مباشر:** إرسال الرابط للمدير لعرض البوستر فوراً
4. **طباعة احترافية:** طباعة في "AZAW TEAM CENTER"

---

## ⚠️ ملاحظات مهمة | Important Notes

1. **centerId = userId:** `centerId` هو نفسه `user_id` في جدول `user_profiles`
2. **البيانات المطلوبة:** يجب أن يحتوي المركز على `health_center_name` و `manager_name`
3. **الشعار:** يجب أن يكون `logo.jpg` موجوداً في `static/assets/logos/`
4. **الجودة:** جميع البوسترات بجودة 300 DPI للطباعة

---

## 🔍 معالجة الأخطاء | Error Handling

### المركز غير موجود:
```json
HTTP 404 Not Found
```

### بيانات ناقصة:
```json
HTTP 400 Bad Request
"Missing required data: health_center_name or manager_name"
```

### خطأ في التوليد:
```json
HTTP 500 Internal Server Error
"Error generating welcome poster: ..."
```

---

## ✨ الخلاصة | Summary

**الميزة الجديدة جاهزة للاستخدام!**

**New feature is ready to use!**

**كل مركز صحي له رابط فريد لعرض بوستر الترحيب الخاص به:**

**Each health center has a unique link to view their welcome poster:**

```
GET /api/posters/welcome/{centerId}
```

**النتيجة:** صورة PNG بجودة 300 DPI جاهزة للطباعة!

**Result:** PNG image at 300 DPI ready for printing!

---

**تم التطوير لدائرة صحة كركوك – قطاع كركوك الأول**  
**Developed for Kirkuk Health Directorate - First Sector**

