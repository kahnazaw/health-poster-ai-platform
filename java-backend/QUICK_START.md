# Quick Start Guide | دليل البدء السريع

## Prerequisites | المتطلبات

- Java 20 JDK
- Maven 3.9+
- PostgreSQL 14+
- Git

## Local Setup | الإعداد المحلي

### 1. Navigate to Java Backend
```bash
cd java-backend
```

### 2. Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/health_poster_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3. Create Database Table
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

### 4. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

### 5. Test API
```bash
# Get MOH topics
curl http://localhost:8080/api/templates/moh-topics

# Get profile (default user)
curl -H "X-User-Id: test-user" http://localhost:8080/api/profile

# Generate poster
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

## Railway Deployment | النشر على Railway

### 1. Set Environment Variables
In Railway dashboard:
- `DATABASE_URL`: Your PostgreSQL connection string
- `PORT`: Auto-set by Railway

### 2. Deploy
Railway will automatically:
- Detect `nixpacks.toml`
- Build with Maven
- Run the JAR file

### 3. Verify Deployment
```bash
curl https://your-app.railway.app/api/templates/moh-topics
```

## Logo Setup | إعداد الشعار

1. Place logo in: `src/main/resources/static/assets/logos/kirkuk-health-logo.png`
2. Update profile:
```bash
curl -X PUT http://localhost:8080/api/profile \
  -H "Content-Type: application/json" \
  -H "X-User-Id: test-user" \
  -d '{
    "logoPath": "assets/logos/kirkuk-health-logo.png",
    "healthCenterName": "مركز صحي كركوك",
    "managerName": "د. أحمد محمد",
    "directorateName": "دائرة صحة كركوك – قطاع كركوك الأول"
  }'
```

## API Examples | أمثلة API

### Generate Poster with MOH Guidelines
```json
POST /api/posters/generate
{
  "topic": "التغذية الصحية",
  "title": "التغذية الصحية: مفتاح الصحة والعافية",
  "mainMessage": "تناول وجبات متوازنة للحفاظ على صحتك",
  "bulletPoints": [
    "تناول 5 حصص من الفواكه والخضروات يومياً",
    "تقليل الملح إلى أقل من 5 جرام يومياً"
  ],
  "closing": "للمزيد من المعلومات، يرجى زيارة أقرب مركز صحي",
  "language": "ar",
  "useMOHGuidelines": true
}
```

### Response
```json
{
  "posterId": "uuid-here",
  "imageUrl": "data:image/png;base64,...",
  "mohApproved": true,
  "verificationBadge": true,
  "content": {
    "title": "...",
    "topic": "...",
    "bulletPoints": [...]
  }
}
```

## Troubleshooting | استكشاف الأخطاء

### Database Connection Error
- Check `DATABASE_URL` format: `jdbc:postgresql://host:port/database`
- Verify PostgreSQL is running
- Check credentials

### Image Generation Fails
- Ensure logo file exists if `logoPath` is set
- Check file permissions
- Verify image format (PNG/JPG)

### Port Already in Use
- Change port in `application.properties`: `server.port=8081`
- Or kill process using port 8080

---

**Ready to generate health posters!**  
**جاهز لتوليد بوسترات التوعية الصحية!**

