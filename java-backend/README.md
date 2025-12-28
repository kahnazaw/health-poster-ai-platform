# Health Poster AI Platform - Java Backend

## منصة توليد بوسترات التوعية الصحية بالذكاء الاصطناعي - الواجهة الخلفية Java

### Overview | نظرة عامة

This is the Java backend for the Health Poster AI Platform, specialized for **Kirkuk Health Directorate - First Sector** (دائرة صحة كركوك – قطاع كركوك الأول).

This backend provides:
- **Dynamic Branding**: Logo overlay and custom footer with Health Center and Manager information
- **Poster Generation**: Canvas-based image generation using Java Graphics2D
- **MOH Guidelines**: Content validation against Iraqi Ministry of Health protocols
- **Multi-language Support**: Arabic, Kurdish, Turkmen, and English text rendering
- **Profile Management**: User profile settings for Health Promotion Units

### Features | المميزات

#### 1. Dynamic Branding | العلامة التجارية الديناميكية
- Integrates Kirkuk Health Directorate logo
- Customizable footer with Health Center name and Manager name
- Verification badge when manager name is present

#### 2. Poster Layout Engine | محرك تخطيط البوسترات
- Uses Java Graphics2D for high-quality image generation
- Standard A4 format (2480x3508 pixels at 300 DPI)
- Automatic text wrapping for RTL languages
- Professional color scheme

#### 3. Content Authority | سلطة المحتوى
- Validates content against Iraqi MOH guidelines
- Enhances content with official health protocols
- Cultural context for Kirkuk/Iraq

#### 4. Multi-language Support | دعم متعدد اللغات
- Arabic (ar) - RTL
- Kurdish (ku) - RTL
- Turkmen (tr) - RTL
- English (en) - LTR

### Technology Stack | التقنيات المستخدمة

- **Java 20**
- **Spring Boot 3.2.0**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL**
- **Graphics2D** (Image processing)
- **ICU4J** (Multi-language text rendering)
- **Maven** (Build tool)

### Project Structure | هيكل المشروع

```
java-backend/
├── src/main/java/com/kirkukhealth/poster/
│   ├── controller/          # REST API Controllers
│   │   ├── PosterController.java
│   │   ├── UserProfileController.java
│   │   └── TemplateController.java
│   ├── service/            # Business Logic Services
│   │   ├── PosterImageService.java
│   │   ├── PosterGenerationService.java
│   │   ├── UserProfileService.java
│   │   ├── ContentAuthorityService.java
│   │   └── MultiLanguageTextService.java
│   ├── repository/         # Data Access Layer
│   │   └── UserProfileRepository.java
│   ├── model/              # Entity Models
│   │   ├── UserProfile.java
│   │   └── PosterContent.java
│   ├── dto/                # Data Transfer Objects
│   │   ├── PosterGenerationRequest.java
│   │   ├── PosterGenerationResponse.java
│   │   ├── UserProfileRequest.java
│   │   └── UserProfileResponse.java
│   └── HealthPosterAiPlatformApplication.java
├── src/main/resources/
│   ├── application.properties
│   └── application.yml
├── pom.xml
├── Dockerfile
├── nixpacks.toml
└── railway.json
```

### API Endpoints | نقاط النهاية

#### Poster Generation | توليد البوستر
```
POST /api/posters/generate
Headers: X-User-Id: {userId}
Body: PosterGenerationRequest
Response: PosterGenerationResponse (with base64 image)
```

#### User Profile | الملف الشخصي
```
GET /api/profile
Headers: X-User-Id: {userId}
Response: UserProfileResponse

PUT /api/profile
Headers: X-User-Id: {userId}
Body: UserProfileRequest
Response: UserProfileResponse
```

#### Templates | القوالب
```
GET /api/templates/moh-topics
Response: List of MOH-approved topics

GET /api/templates/moh-guidelines/{topic}
Response: MOH guidelines for topic

GET /api/templates?type={global|moh|all}
Response: Available templates
```

### Configuration | الإعدادات

#### Environment Variables | متغيرات البيئة

- `PORT`: Server port (default: 8080)
- `DATABASE_URL`: PostgreSQL connection string
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password

#### Railway Deployment | النشر على Railway

1. **Set Environment Variables**:
   - `DATABASE_URL`: Your PostgreSQL connection string
   - `PORT`: Railway will set this automatically

2. **Build Configuration**:
   - Uses `nixpacks.toml` for build process
   - Maven builds the JAR file
   - Java 20 runtime

3. **Start Command**:
   ```bash
   java -jar target/health-poster-ai-platform-1.0.0.jar
   ```

### Local Development | التطوير المحلي

#### Prerequisites | المتطلبات
- Java 20 JDK
- Maven 3.9+
- PostgreSQL 14+

#### Setup | الإعداد

1. **Clone and navigate**:
   ```bash
   cd java-backend
   ```

2. **Configure database**:
   Update `src/main/resources/application.properties` with your database credentials

3. **Build**:
   ```bash
   mvn clean install
   ```

4. **Run**:
   ```bash
   mvn spring-boot:run
   ```

5. **Test**:
   ```bash
   curl http://localhost:8080/api/templates/moh-topics
   ```

### Database Schema | مخطط قاعدة البيانات

The `UserProfile` entity requires a table:
```sql
CREATE TABLE user_profiles (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    health_center_name VARCHAR(200),
    manager_name VARCHAR(150),
    directorate_name VARCHAR(200),
    logo_path VARCHAR(500),
    show_verification_badge BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Logo Setup | إعداد الشعار

1. Place logo files in: `src/main/resources/static/assets/logos/`
2. Supported formats: PNG, JPG
3. Recommended size: 300x300 pixels
4. Set `logoPath` in user profile to: `assets/logos/kirkuk-health-logo.png`

### MOH Guidelines | إرشادات وزارة الصحة

The platform includes pre-configured MOH guidelines for:
- نظافة الأسنان (Dental Health)
- التغذية الصحية (Healthy Nutrition)
- الوقاية من السكري (Diabetes Prevention)
- الإقلاع عن التدخين (Smoking Cessation)
- التطعيم (Vaccination)

### Maintenance & Support | الصيانة والدعم

#### 📚 Maintenance Guide | دليل الصيانة

For comprehensive maintenance and operations documentation, see:

**`MAINTENANCE_GUIDE_AR.md`** - Complete maintenance guide in Arabic and English

This guide includes:
- ✅ How to add/update the 23 health centers
- ✅ How to replace logo.jpg while maintaining 300 DPI quality
- ✅ How to access and interpret the Official CSV Activity Report
- ✅ Railway deployment management (restart, environment variables)
- ✅ Database maintenance and troubleshooting
- ✅ Step-by-step instructions for non-programmers

**Quick Links:**
- [Maintenance Guide](MAINTENANCE_GUIDE_AR.md) - دليل الصيانة الكامل
- [Statistics Guide](STATISTICS_GUIDE.md) - دليل الإحصائيات
- [Quality Audit Report](QUALITY_AUDIT_REPORT.md) - تقرير مراجعة الجودة

#### 🔧 Common Maintenance Tasks | مهام الصيانة الشائعة

**1. Add/Update Health Center:**
```sql
-- See MAINTENANCE_GUIDE_AR.md Section 1 for detailed instructions
```

**2. Replace Logo:**
```
Place new logo.jpg in: src/main/resources/static/assets/logos/logo.jpg
Then: mvn clean install && mvn spring-boot:run
```

**3. Access CSV Report:**
```
GET /api/admin/statistics/export
```

**4. Railway Management:**
- Restart: Railway Dashboard → Service → Restart
- Environment Variables: Railway Dashboard → Variables
- Logs: Railway Dashboard → Deployments → View Logs

### License | الترخيص

This project is proprietary software for Kirkuk Health Directorate.

### Support | الدعم

For issues or questions:
1. Check `MAINTENANCE_GUIDE_AR.md` for step-by-step solutions
2. Review logs in Railway Dashboard
3. Contact the development team

---

**Developed for Kirkuk Health Directorate - First Sector**  
**تم التطوير لدائرة صحة كركوك – قطاع كركوك الأول**

