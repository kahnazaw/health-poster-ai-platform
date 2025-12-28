-- Database Migration: Seed 66 Health Topics
-- إدراج 66 موضوع صحي في قاعدة البيانات

-- Create health_topics table if it doesn't exist
CREATE TABLE IF NOT EXISTS health_topics (
    topic_id VARCHAR(255) PRIMARY KEY,
    topic_name VARCHAR(255) NOT NULL,
    category_name VARCHAR(255) NOT NULL,
    topic_order INTEGER,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_health_topics UNIQUE (topic_name, category_name)
);

COMMENT ON TABLE health_topics IS 'المواضيع الصحية (66 موضوع) من إرشادات وزارة الصحة العراقية';
COMMENT ON COLUMN health_topics.topic_id IS 'معرف الموضوع';
COMMENT ON COLUMN health_topics.topic_name IS 'اسم الموضوع';
COMMENT ON COLUMN health_topics.category_name IS 'اسم الفئة';
COMMENT ON COLUMN health_topics.topic_order IS 'ترتيب الموضوع في الفئة';

-- Insert 66 Health Topics from 11 Categories
-- 1. Maternal & Child Health | صحة الأم والطفل
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_001', 'رعاية ما قبل الولادة', 'صحة الأم والطفل', 1),
('topic_002', 'رعاية ما بعد الولادة', 'صحة الأم والطفل', 2),
('topic_003', 'الرضاعة الطبيعية', 'صحة الأم والطفل', 3),
('topic_004', 'تغذية الرضع والأطفال', 'صحة الأم والطفل', 4),
('topic_005', 'نمو وتطور الطفل', 'صحة الأم والطفل', 5),
('topic_006', 'صحة المراهقين', 'صحة الأم والطفل', 6)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 2. Immunization | التطعيم
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_007', 'التطعيم الروتيني', 'التطعيم', 1),
('topic_008', 'التطعيم الموسمي', 'التطعيم', 2),
('topic_009', 'أهمية التطعيم', 'التطعيم', 3),
('topic_010', 'جدول التطعيمات', 'التطعيم', 4),
('topic_011', 'التطعيم للأطفال', 'التطعيم', 5),
('topic_012', 'التطعيم للبالغين', 'التطعيم', 6)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 3. Communicable Diseases | الأمراض المعدية
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_013', 'الوقاية من الإنفلونزا', 'الأمراض المعدية', 1),
('topic_014', 'الوقاية من COVID-19', 'الأمراض المعدية', 2),
('topic_015', 'الوقاية من السل', 'الأمراض المعدية', 3),
('topic_016', 'الوقاية من التهاب الكبد', 'الأمراض المعدية', 4),
('topic_017', 'الوقاية من الملاريا', 'الأمراض المعدية', 5),
('topic_018', 'النظافة الشخصية للوقاية', 'الأمراض المعدية', 6),
('topic_019', 'التباعد الاجتماعي', 'الأمراض المعدية', 7)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 4. Non-Communicable Diseases | الأمراض غير المعدية
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_020', 'الوقاية من السكري', 'الأمراض غير المعدية', 1),
('topic_021', 'الوقاية من أمراض القلب', 'الأمراض غير المعدية', 2),
('topic_022', 'الوقاية من السرطان', 'الأمراض غير المعدية', 3),
('topic_023', 'الوقاية من ارتفاع ضغط الدم', 'الأمراض غير المعدية', 4),
('topic_024', 'الوقاية من السمنة', 'الأمراض غير المعدية', 5),
('topic_025', 'النشاط البدني', 'الأمراض غير المعدية', 6),
('topic_026', 'التغذية الصحية', 'الأمراض غير المعدية', 7)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 5. Mental Health | الصحة النفسية
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_027', 'الصحة النفسية العامة', 'الصحة النفسية', 1),
('topic_028', 'التعامل مع التوتر', 'الصحة النفسية', 2),
('topic_029', 'الصحة النفسية للأطفال', 'الصحة النفسية', 3),
('topic_030', 'الصحة النفسية للمراهقين', 'الصحة النفسية', 4),
('topic_031', 'الصحة النفسية لكبار السن', 'الصحة النفسية', 5),
('topic_032', 'الوقاية من الاكتئاب', 'الصحة النفسية', 6)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 6. First Aid | الإسعافات الأولية
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_033', 'الإسعافات الأولية الأساسية', 'الإسعافات الأولية', 1),
('topic_034', 'الإسعافات الأولية للأطفال', 'الإسعافات الأولية', 2),
('topic_035', 'الإسعافات الأولية في حالات الطوارئ', 'الإسعافات الأولية', 3),
('topic_036', 'إنعاش القلب والرئتين (CPR)', 'الإسعافات الأولية', 4),
('topic_037', 'معالجة الجروح', 'الإسعافات الأولية', 5),
('topic_038', 'معالجة الحروق', 'الإسعافات الأولية', 6)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 7. Hygiene | النظافة
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_039', 'النظافة الشخصية', 'النظافة', 1),
('topic_040', 'نظافة الأسنان', 'النظافة', 2),
('topic_041', 'نظافة اليدين', 'النظافة', 3),
('topic_042', 'نظافة البيئة', 'النظافة', 4),
('topic_043', 'نظافة المياه', 'النظافة', 5),
('topic_044', 'نظافة الغذاء', 'النظافة', 6)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 8. Medication Misuse | سوء استخدام الأدوية
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_045', 'الاستخدام الصحيح للأدوية', 'سوء استخدام الأدوية', 1),
('topic_046', 'الجرعات الصحيحة', 'سوء استخدام الأدوية', 2),
('topic_047', 'تخزين الأدوية', 'سوء استخدام الأدوية', 3),
('topic_048', 'مخاطر إساءة استخدام الأدوية', 'سوء استخدام الأدوية', 4),
('topic_049', 'التداخل الدوائي', 'سوء استخدام الأدوية', 5),
('topic_050', 'التخلص من الأدوية المنتهية', 'سوء استخدام الأدوية', 6)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 9. Antimicrobial Resistance | مقاومة المضادات الحيوية
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_051', 'الاستخدام الصحيح للمضادات الحيوية', 'مقاومة المضادات الحيوية', 1),
('topic_052', 'مخاطر مقاومة المضادات الحيوية', 'مقاومة المضادات الحيوية', 2),
('topic_053', 'الوقاية من مقاومة المضادات الحيوية', 'مقاومة المضادات الحيوية', 3),
('topic_054', 'متى تستخدم المضادات الحيوية', 'مقاومة المضادات الحيوية', 4),
('topic_055', 'إكمال دورة المضادات الحيوية', 'مقاومة المضادات الحيوية', 5)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 10. Health Occasions | المناسبات الصحية
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_056', 'اليوم العالمي للصحة', 'المناسبات الصحية', 1),
('topic_057', 'اليوم العالمي لمكافحة التدخين', 'المناسبات الصحية', 2),
('topic_058', 'اليوم العالمي للسكري', 'المناسبات الصحية', 3),
('topic_059', 'اليوم العالمي للقلب', 'المناسبات الصحية', 4),
('topic_060', 'أسبوع التطعيم', 'المناسبات الصحية', 5),
('topic_061', 'أسبوع الرضاعة الطبيعية', 'المناسبات الصحية', 6),
('topic_062', 'مناسبات صحية أخرى', 'المناسبات الصحية', 7)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- 11. Other | أخرى
INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order) VALUES
('topic_063', 'مواضيع صحية عامة', 'أخرى', 1),
('topic_064', 'الوعي الصحي', 'أخرى', 2),
('topic_065', 'الخدمات الصحية المتاحة', 'أخرى', 3),
('topic_066', 'مواضيع أخرى', 'أخرى', 4)
ON CONFLICT (topic_name, category_name) DO NOTHING;

-- Create indexes for faster queries
CREATE INDEX IF NOT EXISTS idx_health_topics_category ON health_topics(category_name);
CREATE INDEX IF NOT EXISTS idx_health_topics_active ON health_topics(is_active);

-- Verify insertion
SELECT category_name, COUNT(*) as topic_count 
FROM health_topics 
GROUP BY category_name 
ORDER BY category_name;

