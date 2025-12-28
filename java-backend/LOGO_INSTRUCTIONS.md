# Logo Setup - Quick Guide | دليل سريع لإعداد الشعار

## Step 1: Add the Logo File | الخطوة 1: إضافة ملف الشعار

Place your Kirkuk Health Directorate logo image in:
ضع صورة شعار دائرة صحة كركوك في:

```
java-backend/src/main/resources/static/assets/logos/kirkuk-health-directorate-logo.png
```

## Step 2: Verify File | الخطوة 2: التحقق من الملف

The logo file should be:
- **Format**: PNG (recommended) or JPG
- **Size**: 300x300 to 1200x1200 pixels (square)
- **Name**: `kirkuk-health-directorate-logo.png`

## Step 3: Rebuild | الخطوة 3: إعادة البناء

```bash
cd java-backend
mvn clean install
```

## Step 4: Test | الخطوة 4: الاختبار

The logo will automatically appear on all generated posters!

For detailed instructions, see: `src/main/resources/static/assets/logos/LOGO_SETUP.md`

---

**The system will automatically use this logo for all poster generation!**  
**سيستخدم النظام هذا الشعار تلقائياً لجميع البوسترات المولدة!**

