-- ============================================================================
-- Database Migration V5: Create monthly_targets table
-- ============================================================================
-- إنشاء جدول الأهداف الشهرية
-- Create table for monthly targets
--
-- Purpose: Track monthly targets for each health center by category and topic
-- الغرض: تتبع الأهداف الشهرية لكل مركز صحي حسب الفئة والموضوع
--
-- Targets are set for:
-- - Individual Meetings (اللقاءات الفردية)
-- - Lectures (المحاضرات)
-- - Seminars (الندوات)
-- ============================================================================

CREATE TABLE IF NOT EXISTS monthly_targets (
    id VARCHAR(255) PRIMARY KEY,
    center_id VARCHAR(255) NOT NULL,
    category_name VARCHAR(200) NOT NULL,
    topic_name VARCHAR(300) NOT NULL,
    target_meetings INTEGER NOT NULL DEFAULT 0,
    target_lectures INTEGER NOT NULL DEFAULT 0,
    target_seminars INTEGER NOT NULL DEFAULT 0,
    target_month INTEGER NOT NULL,
    target_year INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Unique constraint: one target per center, category, topic, month, and year
    CONSTRAINT uk_monthly_targets UNIQUE (center_id, category_name, topic_name, target_month, target_year)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_monthly_targets_center_id ON monthly_targets(center_id);
CREATE INDEX IF NOT EXISTS idx_monthly_targets_month_year ON monthly_targets(target_year, target_month);
CREATE INDEX IF NOT EXISTS idx_monthly_targets_category ON monthly_targets(category_name);
CREATE INDEX IF NOT EXISTS idx_monthly_targets_center_month_year ON monthly_targets(center_id, target_year, target_month);

-- Add comments
COMMENT ON TABLE monthly_targets IS 'الأهداف الشهرية للمراكز الصحية | Monthly targets for health centers';
COMMENT ON COLUMN monthly_targets.center_id IS 'معرف المركز الصحي | Health center ID';
COMMENT ON COLUMN monthly_targets.category_name IS 'اسم الفئة | Category name';
COMMENT ON COLUMN monthly_targets.topic_name IS 'اسم الموضوع | Topic name';
COMMENT ON COLUMN monthly_targets.target_meetings IS 'هدف اللقاءات الفردية | Target for individual meetings';
COMMENT ON COLUMN monthly_targets.target_lectures IS 'هدف المحاضرات | Target for lectures';
COMMENT ON COLUMN monthly_targets.target_seminars IS 'هدف الندوات | Target for seminars';
COMMENT ON COLUMN monthly_targets.target_month IS 'الشهر المستهدف (1-12) | Target month (1-12)';
COMMENT ON COLUMN monthly_targets.target_year IS 'السنة المستهدفة | Target year';

