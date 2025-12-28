# Architecture Overview | نظرة عامة على البنية

## System Architecture | بنية النظام

### Core Components | المكونات الأساسية

#### 1. **PosterImageService** - Image Generation Engine
   - **Purpose**: Generates poster images using Java Graphics2D
   - **Features**:
     - Logo overlay at top
     - Dynamic footer with Health Center and Manager info
     - Verification badge rendering
     - Multi-language text support (RTL/LTR)
     - A4 format (2480x3508 @ 300 DPI)

#### 2. **ContentAuthorityService** - MOH Guidelines Enforcement
   - **Purpose**: Ensures content follows Iraqi Ministry of Health protocols
   - **Features**:
     - Pre-configured MOH guidelines for health topics
     - Content validation
     - Cultural context enhancement for Kirkuk/Iraq
     - Topic approval checking

#### 3. **UserProfileService** - Profile Management
   - **Purpose**: Manages Health Promotion Unit settings
   - **Features**:
     - Health Center Name
     - Manager Name
     - Directorate Name
     - Logo path configuration
     - Verification badge toggle

#### 4. **MultiLanguageTextService** - Text Rendering
   - **Purpose**: Handles RTL/LTR text rendering
   - **Features**:
     - Arabic, Kurdish, Turkmen (RTL)
     - English (LTR)
     - Font selection per language
     - Text wrapping and direction detection

#### 5. **PosterGenerationService** - Orchestration
   - **Purpose**: Coordinates poster generation workflow
   - **Workflow**:
     1. Get/Create user profile
     2. Enhance content with MOH guidelines
     3. Add cultural context
     4. Validate content
     5. Generate image
     6. Return result

### API Layer | طبقة API

#### REST Controllers:
- **PosterController**: `/api/posters/*`
  - `POST /generate` - Generate poster
  - `GET /{id}/download` - Download poster image

- **UserProfileController**: `/api/profile`
  - `GET /` - Get profile
  - `PUT /` - Update profile

- **TemplateController**: `/api/templates/*`
  - `GET /moh-topics` - List MOH topics
  - `GET /moh-guidelines/{topic}` - Get guidelines
  - `GET /?type={global|moh|all}` - Get templates

### Data Layer | طبقة البيانات

#### Entities:
- **UserProfile**: Stores Health Promotion Unit settings
  - Fields: healthCenterName, managerName, directorateName, logoPath, showVerificationBadge

#### Repository:
- **UserProfileRepository**: JPA repository for UserProfile operations

### Configuration | الإعدادات

#### Application Properties:
- Database connection (PostgreSQL)
- JPA/Hibernate settings
- File upload limits
- CORS configuration
- Logging levels

#### Railway Deployment:
- `nixpacks.toml` - Build configuration
- `Dockerfile` - Container image
- `railway.json` - Railway-specific settings

### Image Generation Flow | تدفق توليد الصورة

```
1. Request received with PosterContent
   ↓
2. Get UserProfile (or create default)
   ↓
3. Enhance content with MOH guidelines
   ↓
4. Add cultural context
   ↓
5. Validate content
   ↓
6. Create BufferedImage (2480x3508)
   ↓
7. Draw header with logo
   ↓
8. Draw main content (title, message, bullets, closing)
   ↓
9. Draw footer (Health Center, Manager)
   ↓
10. Draw verification badge (if enabled)
   ↓
11. Convert to PNG byte array
   ↓
12. Return base64-encoded image
```

### MOH Guidelines Integration | تكامل إرشادات وزارة الصحة

The system includes pre-configured guidelines for:
- نظافة الأسنان (Dental Health)
- التغذية الصحية (Healthy Nutrition)
- الوقاية من السكري (Diabetes Prevention)
- الإقلاع عن التدخين (Smoking Cessation)
- التطعيم (Vaccination)

Each topic has:
- Official MOH-approved bullet points
- Validation rules
- Cultural context for Iraq/Kirkuk

### Multi-language Support | دعم متعدد اللغات

#### Supported Languages:
- **Arabic (ar)**: RTL, Arial Unicode MS
- **Kurdish (ku)**: RTL, Arial Unicode MS
- **Turkmen (tr)**: RTL, Arial Unicode MS
- **English (en)**: LTR, Arial

#### Text Rendering:
- Automatic RTL/LTR detection
- Proper text wrapping
- Font selection per language
- Direction-aware layout

### Security Considerations | اعتبارات الأمان

1. **Authentication**: Currently uses `X-User-Id` header (should be replaced with JWT in production)
2. **CORS**: Configured for all origins (restrict in production)
3. **File Upload**: Limited to 10MB
4. **Database**: Uses parameterized queries (JPA)

### Performance Optimizations | تحسينات الأداء

1. **Image Generation**: Single-pass rendering with Graphics2D
2. **Database**: Connection pooling (HikariCP)
3. **Caching**: Can be added for frequently accessed profiles
4. **Async Processing**: Can be added for large poster batches

### Future Enhancements | التحسينات المستقبلية

1. **AI Integration**: Connect to OpenAI/Claude for content generation
2. **Template System**: Pre-designed poster templates
3. **Batch Generation**: Generate multiple posters at once
4. **Export Formats**: PDF, JPG, SVG support
5. **QR Code Integration**: Add QR codes to posters
6. **Analytics**: Track poster usage and downloads

---

**Architecture designed for Kirkuk Health Directorate - First Sector**  
**البنية مصممة لدائرة صحة كركوك – قطاع كركوك الأول**

