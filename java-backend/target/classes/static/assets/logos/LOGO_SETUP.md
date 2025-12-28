# Logo Setup Instructions | تعليمات إعداد الشعار

## Kirkuk Health Directorate Logo | شعار دائرة صحة كركوك

### Logo File Location | موقع ملف الشعار

Place the Kirkuk Health Directorate logo file in this directory:
ضع ملف شعار دائرة صحة كركوك في هذا المجلد:

```
java-backend/src/main/resources/static/assets/logos/kirkuk-health-directorate-logo.png
```

### Supported File Names | أسماء الملفات المدعومة

The system will automatically look for the logo using these names (in order):
سيبحث النظام تلقائياً عن الشعار باستخدام هذه الأسماء (بالترتيب):

1. `kirkuk-health-directorate-logo.png` (Primary - recommended)
2. `kirkuk-health-logo.png`
3. `logo.png`
4. `kirkuk-logo.png`

### Logo Specifications | مواصفات الشعار

**Recommended Format**: PNG with transparent background
**التنسيق الموصى به**: PNG مع خلفية شفافة

**Recommended Size**: 
- Minimum: 300x300 pixels
- Optimal: 600x600 pixels
- Maximum: 1200x1200 pixels

**Color Mode**: RGB or RGBA (with transparency)

### Logo Description | وصف الشعار

The Kirkuk Health Directorate logo features:
يتميز شعار دائرة صحة كركوك بـ:

- **Circular design** with white borders
- **Red crescent moon** on the left (health symbol)
- **Black oil derrick with yellow/orange flame** on the right (regional identity)
- **Multi-language text** in green:
  - Arabic: "دائرة صحة كركوك"
  - English: "KIRKUK HEALTH"
  - Turkish/Kurdish: "KERKÜK SAĞLIK DAİRESİ"
  - Kurdish: "دائرة تەندروستی کەرکوک"

### How to Add the Logo | كيفية إضافة الشعار

1. **Download or obtain** the Kirkuk Health Directorate logo file
2. **Rename** it to `kirkuk-health-directorate-logo.png` (if needed)
3. **Place** it in: `java-backend/src/main/resources/static/assets/logos/`
4. **Rebuild** the application: `mvn clean install`
5. **Restart** the application

### Verification | التحقق

After adding the logo, test it by generating a poster:

```bash
curl -X POST http://localhost:8080/api/posters/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: test-user" \
  -d '{
    "topic": "نظافة الأسنان",
    "title": "نظافة الأسنان: أساس الصحة الفموية",
    "bulletPoints": ["اغسل أسنانك مرتين يومياً"],
    "language": "ar"
  }'
```

The generated poster should include the logo at the top.

### Troubleshooting | استكشاف الأخطاء

**Logo not appearing?**
- Check file is in correct directory
- Verify file name matches one of the supported names
- Ensure file is PNG format
- Check file permissions
- Rebuild application after adding logo

**Logo appears distorted?**
- Use square dimensions (1:1 aspect ratio)
- Use high resolution (600x600 or higher)
- Ensure PNG format with transparency

---

**Note**: The logo will be automatically resized to fit the poster layout (150px height).
**ملاحظة**: سيتم تغيير حجم الشعار تلقائياً ليتناسب مع تخطيط البوستر (150 بكسل ارتفاع).

