-- Database Migration: Create health_occasions table
-- جدول المناسبات الصحية العالمية

CREATE TABLE IF NOT EXISTS health_occasions (
    occasion_id VARCHAR(255) PRIMARY KEY,
    occasion_name_ar VARCHAR(255) NOT NULL,
    occasion_name_en VARCHAR(255),
    occasion_date DATE NOT NULL,
    is_week_long BOOLEAN DEFAULT FALSE,
    end_date DATE,
    category VARCHAR(100),
    description_ar TEXT,
    suggested_topics TEXT,
    priority INTEGER DEFAULT 5,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE health_occasions IS 'المناسبات الصحية العالمية (30+ مناسبة)';
COMMENT ON COLUMN health_occasions.occasion_id IS 'معرف المناسبة';
COMMENT ON COLUMN health_occasions.occasion_name_ar IS 'اسم المناسبة بالعربية';
COMMENT ON COLUMN health_occasions.occasion_name_en IS 'اسم المناسبة بالإنجليزية';
COMMENT ON COLUMN health_occasions.occasion_date IS 'تاريخ المناسبة';
COMMENT ON COLUMN health_occasions.is_week_long IS 'هل المناسبة تمتد لأسبوع كامل؟';
COMMENT ON COLUMN health_occasions.end_date IS 'تاريخ نهاية المناسبة (للمناسبات الأسبوعية)';
COMMENT ON COLUMN health_occasions.category IS 'فئة المناسبة (مثل: الأمراض المعدية، الصحة النفسية)';
COMMENT ON COLUMN health_occasions.description_ar IS 'وصف المناسبة بالعربية';
COMMENT ON COLUMN health_occasions.suggested_topics IS 'المواضيع المقترحة المرتبطة بالمناسبة (مفصولة بفواصل)';
COMMENT ON COLUMN health_occasions.priority IS 'الأولوية (1-10، الأعلى = الأهم)';
COMMENT ON COLUMN health_occasions.is_active IS 'هل المناسبة نشطة؟';

-- Insert 30+ Global Health Occasions
-- إدراج 30+ مناسبة صحية عالمية

-- January
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_001', 'اليوم العالمي للأمراض المدارية المهملة', 'World Neglected Tropical Diseases Day', '2024-01-30', FALSE, 'الأمراض المعدية', 'يوم للتوعية بالأمراض المدارية المهملة', 'الوقاية من الملاريا,الوقاية من التهاب الكبد,النظافة الشخصية للوقاية', 8);

-- February
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_002', 'اليوم العالمي للسرطان', 'World Cancer Day', '2024-02-04', FALSE, 'الأمراض غير المعدية', 'يوم للتوعية بمرض السرطان والوقاية منه', 'الوقاية من السرطان,التغذية الصحية,النشاط البدني', 10);

-- March
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_003', 'اليوم العالمي للسل', 'World TB Day', '2024-03-24', FALSE, 'الأمراض المعدية', 'يوم للتوعية بمرض السل والوقاية منه', 'الوقاية من السل,النظافة الشخصية للوقاية,التباعد الاجتماعي', 9);

-- April
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_004', 'اليوم العالمي للصحة', 'World Health Day', '2024-04-07', FALSE, 'أخرى', 'يوم الصحة العالمي', 'الوعي الصحي,الخدمات الصحية المتاحة,مواضيع صحية عامة', 10),
('occ_005', 'أسبوع التطعيم العالمي', 'World Immunization Week', '2024-04-24', TRUE, 'التطعيم', 'أسبوع التطعيم العالمي', 'التطعيم الروتيني,التطعيم الموسمي,أهمية التطعيم,جدول التطعيمات', 10);
-- Update end_date for week-long occasion
UPDATE health_occasions SET end_date = '2024-04-30' WHERE occasion_id = 'occ_005';

-- May
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_006', 'اليوم العالمي لمكافحة التدخين', 'World No Tobacco Day', '2024-05-31', FALSE, 'الأمراض غير المعدية', 'يوم للتوعية بمخاطر التدخين', 'الإقلاع عن التدخين,الوقاية من السرطان,الوقاية من أمراض القلب', 9);

-- June
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_007', 'اليوم العالمي للمتبرعين بالدم', 'World Blood Donor Day', '2024-06-14', FALSE, 'أخرى', 'يوم للتوعية بأهمية التبرع بالدم', 'الوعي الصحي,الخدمات الصحية المتاحة', 7);

-- July
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_008', 'اليوم العالمي لالتهاب الكبد', 'World Hepatitis Day', '2024-07-28', FALSE, 'الأمراض المعدية', 'يوم للتوعية بمرض التهاب الكبد', 'الوقاية من التهاب الكبد,النظافة الشخصية للوقاية', 8);

-- August
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_009', 'أسبوع الرضاعة الطبيعية العالمي', 'World Breastfeeding Week', '2024-08-01', TRUE, 'صحة الأم والطفل', 'أسبوع الرضاعة الطبيعية العالمي', 'الرضاعة الطبيعية,رعاية ما بعد الولادة,تغذية الرضع والأطفال', 9);
-- Update end_date for week-long occasion
UPDATE health_occasions SET end_date = '2024-08-07' WHERE occasion_id = 'occ_009';

-- September
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_010', 'اليوم العالمي لداء الكلب', 'World Rabies Day', '2024-09-28', FALSE, 'الأمراض المعدية', 'يوم للتوعية بداء الكلب', 'الوقاية من الأمراض المعدية,النظافة الشخصية للوقاية', 7);

-- October
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_011', 'اليوم العالمي للصحة النفسية', 'World Mental Health Day', '2024-10-10', FALSE, 'الصحة النفسية', 'يوم للتوعية بالصحة النفسية', 'الصحة النفسية العامة,التعامل مع التوتر,الوقاية من الاكتئاب', 9);

-- November
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_012', 'اليوم العالمي للسكري', 'World Diabetes Day', '2024-11-14', FALSE, 'الأمراض غير المعدية', 'يوم للتوعية بمرض السكري', 'الوقاية من السكري,التغذية الصحية,النشاط البدني', 9);

-- December
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_013', 'اليوم العالمي للإيدز', 'World AIDS Day', '2024-12-01', FALSE, 'الأمراض المعدية', 'يوم للتوعية بمرض الإيدز', 'الوقاية من الأمراض المعدية,النظافة الشخصية للوقاية', 9);

-- Additional occasions
INSERT INTO health_occasions (occasion_id, occasion_name_ar, occasion_name_en, occasion_date, is_week_long, category, description_ar, suggested_topics, priority) VALUES
('occ_014', 'اليوم العالمي للقلب', 'World Heart Day', '2024-09-29', FALSE, 'الأمراض غير المعدية', 'يوم للتوعية بأمراض القلب', 'الوقاية من أمراض القلب,التغذية الصحية,النشاط البدني', 8),
('occ_015', 'اليوم العالمي للوقاية من السمنة', 'World Obesity Day', '2024-03-04', FALSE, 'الأمراض غير المعدية', 'يوم للتوعية بالسمنة', 'الوقاية من السمنة,التغذية الصحية,النشاط البدني', 7),
('occ_016', 'اليوم العالمي للوقاية من ارتفاع ضغط الدم', 'World Hypertension Day', '2024-05-17', FALSE, 'الأمراض غير المعدية', 'يوم للتوعية بارتفاع ضغط الدم', 'الوقاية من ارتفاع ضغط الدم,التغذية الصحية,النشاط البدني', 7),
('occ_017', 'اليوم العالمي للوقاية من COVID-19', 'COVID-19 Awareness Day', '2024-03-11', FALSE, 'الأمراض المعدية', 'يوم للتوعية بمرض COVID-19', 'الوقاية من COVID-19,التباعد الاجتماعي,النظافة الشخصية للوقاية', 10),
('occ_018', 'اليوم العالمي للوقاية من الإنفلونزا', 'World Flu Day', '2024-10-01', FALSE, 'الأمراض المعدية', 'يوم للتوعية بالإنفلونزا', 'الوقاية من الإنفلونزا,التطعيم الموسمي,النظافة الشخصية للوقاية', 7),
('occ_019', 'اليوم العالمي للوقاية من الكوليرا', 'Cholera Prevention Day', '2024-10-20', FALSE, 'الأمراض المعدية', 'يوم للتوعية بالكوليرا', 'الوقاية من الأمراض المعدية,نظافة المياه,نظافة الغذاء', 8),
('occ_020', 'اليوم العالمي للصحة النفسية للأطفال', 'Children Mental Health Day', '2024-05-07', FALSE, 'الصحة النفسية', 'يوم للتوعية بالصحة النفسية للأطفال', 'الصحة النفسية للأطفال,نمو وتطور الطفل', 7),
('occ_021', 'اليوم العالمي للصحة النفسية للمراهقين', 'Teen Mental Health Day', '2024-05-18', FALSE, 'الصحة النفسية', 'يوم للتوعية بالصحة النفسية للمراهقين', 'الصحة النفسية للمراهقين,صحة المراهقين', 7),
('occ_022', 'اليوم العالمي للصحة النفسية لكبار السن', 'Elderly Mental Health Day', '2024-10-01', FALSE, 'الصحة النفسية', 'يوم للتوعية بالصحة النفسية لكبار السن', 'الصحة النفسية لكبار السن', 6),
('occ_023', 'اليوم العالمي للإسعافات الأولية', 'World First Aid Day', '2024-09-14', FALSE, 'الإسعافات الأولية', 'يوم للتوعية بالإسعافات الأولية', 'الإسعافات الأولية الأساسية,إنعاش القلب والرئتين (CPR),معالجة الجروح', 8),
('occ_024', 'اليوم العالمي للنظافة', 'World Hygiene Day', '2024-10-15', FALSE, 'النظافة', 'يوم للتوعية بالنظافة', 'النظافة الشخصية,نظافة الأسنان,نظافة اليدين,نظافة البيئة', 7),
('occ_025', 'اليوم العالمي لنظافة الأسنان', 'World Oral Health Day', '2024-03-20', FALSE, 'النظافة', 'يوم للتوعية بنظافة الأسنان', 'نظافة الأسنان,النظافة الشخصية', 7),
('occ_026', 'اليوم العالمي للاستخدام الصحيح للأدوية', 'World Medication Safety Day', '2024-09-17', FALSE, 'سوء استخدام الأدوية', 'يوم للتوعية بالاستخدام الصحيح للأدوية', 'الاستخدام الصحيح للأدوية,الجرعات الصحيحة,تخزين الأدوية', 7),
('occ_027', 'اليوم العالمي لمقاومة المضادات الحيوية', 'World Antimicrobial Resistance Day', '2024-11-18', FALSE, 'مقاومة المضادات الحيوية', 'يوم للتوعية بمقاومة المضادات الحيوية', 'الاستخدام الصحيح للمضادات الحيوية,مخاطر مقاومة المضادات الحيوية', 8),
('occ_028', 'اليوم العالمي للوقاية من سوء استخدام الأدوية', 'World Drug Misuse Prevention Day', '2024-06-26', FALSE, 'سوء استخدام الأدوية', 'يوم للتوعية بسوء استخدام الأدوية', 'مخاطر إساءة استخدام الأدوية,التداخل الدوائي', 7),
('occ_029', 'اليوم العالمي للرعاية الصحية للأم', 'Maternal Health Day', '2024-05-28', FALSE, 'صحة الأم والطفل', 'يوم للتوعية برعاية الأم', 'رعاية ما قبل الولادة,رعاية ما بعد الولادة', 8),
('occ_030', 'اليوم العالمي للطفل', 'World Children Day', '2024-11-20', FALSE, 'صحة الأم والطفل', 'يوم الطفل العالمي', 'نمو وتطور الطفل,تغذية الرضع والأطفال,صحة المراهقين', 8),
('occ_031', 'اليوم العالمي للوقاية من الحوادث', 'World Accident Prevention Day', '2024-04-28', FALSE, 'الإسعافات الأولية', 'يوم للتوعية بالوقاية من الحوادث', 'الإسعافات الأولية في حالات الطوارئ,معالجة الحروق', 7),
('occ_032', 'اليوم العالمي للوقاية من السقوط', 'World Fall Prevention Day', '2024-09-22', FALSE, 'الإسعافات الأولية', 'يوم للتوعية بالوقاية من السقوط', 'الإسعافات الأولية الأساسية', 6),
('occ_033', 'اليوم العالمي للوقاية من الحروق', 'World Burn Prevention Day', '2024-10-26', FALSE, 'الإسعافات الأولية', 'يوم للتوعية بالوقاية من الحروق', 'معالجة الحروق,الإسعافات الأولية الأساسية', 7),
('occ_034', 'اليوم العالمي للوقاية من الجفاف', 'World Dehydration Prevention Day', '2024-07-24', FALSE, 'النظافة', 'يوم للتوعية بالوقاية من الجفاف', 'نظافة المياه,التغذية الصحية', 6),
('occ_035', 'اليوم العالمي للوقاية من التسمم الغذائي', 'World Food Poisoning Prevention Day', '2024-06-07', FALSE, 'النظافة', 'يوم للتوعية بالوقاية من التسمم الغذائي', 'نظافة الغذاء,نظافة اليدين', 7);

-- Create index for faster date queries
CREATE INDEX IF NOT EXISTS idx_health_occasions_date ON health_occasions(occasion_date);
CREATE INDEX IF NOT EXISTS idx_health_occasions_active ON health_occasions(is_active);


