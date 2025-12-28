-- ============================================================================
-- Database Migration V4: Create daily_statistics table
-- ============================================================================
-- إنشاء جدول الإحصائيات اليومية
-- Create table for daily health statistics
--
-- Purpose: Track daily health promotion activities for 23 health centers
-- الغرض: تتبع أنشطة تعزيز الصحة اليومية لـ 23 مركز صحي
--
-- Activities tracked:
-- - Individual meetings (اللقاءات الفردية)
-- - Lectures (المحاضرات)
-- - Seminars (الندوات)
-- ============================================================================

CREATE TABLE IF NOT EXISTS daily_statistics (
    id VARCHAR(255) PRIMARY KEY,
    center_id VARCHAR(255) NOT NULL,
    category_name VARCHAR(200) NOT NULL,
    topic_name VARCHAR(300) NOT NULL,
    individual_meetings INTEGER NOT NULL DEFAULT 0,
    lectures INTEGER NOT NULL DEFAULT 0,
    seminars INTEGER NOT NULL DEFAULT 0,
    entry_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Unique constraint: one entry per center, category, topic, and date
    CONSTRAINT uk_daily_stats UNIQUE (center_id, category_name, topic_name, entry_date)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_daily_stats_center_id ON daily_statistics(center_id);
CREATE INDEX IF NOT EXISTS idx_daily_stats_entry_date ON daily_statistics(entry_date);
CREATE INDEX IF NOT EXISTS idx_daily_stats_category ON daily_statistics(category_name);
CREATE INDEX IF NOT EXISTS idx_daily_stats_center_date ON daily_statistics(center_id, entry_date);

-- Add comments
COMMENT ON TABLE daily_statistics IS 'الإحصائيات اليومية لأنشطة تعزيز الصحة | Daily health promotion activity statistics';
COMMENT ON COLUMN daily_statistics.center_id IS 'معرف المركز الصحي (مرتبط بـ user_profiles.user_id) | Health center ID';
COMMENT ON COLUMN daily_statistics.category_name IS 'اسم الفئة (11 قسم رئيسي) | Category name (11 main sections)';
COMMENT ON COLUMN daily_statistics.topic_name IS 'اسم الموضوع (موضوع فرعي) | Topic name (sub-topic)';
COMMENT ON COLUMN daily_statistics.individual_meetings IS 'عدد اللقاءات الفردية | Individual meetings count';
COMMENT ON COLUMN daily_statistics.lectures IS 'عدد المحاضرات | Lectures count';
COMMENT ON COLUMN daily_statistics.seminars IS 'عدد الندوات | Seminars count';
COMMENT ON COLUMN daily_statistics.entry_date IS 'تاريخ الإدخال (تاريخ النشاط) | Entry date (activity date)';

