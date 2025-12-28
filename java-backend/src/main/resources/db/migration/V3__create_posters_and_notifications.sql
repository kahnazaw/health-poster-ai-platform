-- ============================================================================
-- Database Migration V3: Create posters and notifications tables
-- ============================================================================
-- إنشاء جداول البوسترات والإشعارات
-- Create tables for poster governance and notifications
--
-- Purpose: Enable poster approval workflow and admin notifications
-- الغرض: تمكين سير عمل الموافقة على البوسترات وإشعارات المدير
--
-- Status Flow: DRAFT -> PENDING -> APPROVED
-- Only Admin (Sector Manager) can approve posters
-- ============================================================================

-- ============================================================================
-- Posters Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS posters (
    id VARCHAR(255) PRIMARY KEY,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    user_id VARCHAR(255) NOT NULL,
    health_center_id VARCHAR(255),
    title VARCHAR(500),
    topic VARCHAR(200),
    content TEXT,
    image_bytes BYTEA,
    moh_approved BOOLEAN,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_posters_status ON posters(status);
CREATE INDEX IF NOT EXISTS idx_posters_user_id ON posters(user_id);
CREATE INDEX IF NOT EXISTS idx_posters_health_center_id ON posters(health_center_id);
CREATE INDEX IF NOT EXISTS idx_posters_created_at ON posters(created_at);

-- Add comments
COMMENT ON TABLE posters IS 'البوسترات مع حالة الحوكمة | Posters with governance status';
COMMENT ON COLUMN posters.status IS 'حالة البوستر: DRAFT, PENDING, APPROVED | Poster status';
COMMENT ON COLUMN posters.image_bytes IS 'صورة عالية الدقة 300 DPI - متاحة فقط عند الموافقة | High-res 300 DPI image - only available when APPROVED';
COMMENT ON COLUMN posters.approved_by IS 'المدير الذي وافق على البوستر | Admin who approved the poster';

-- ============================================================================
-- Notifications Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS notifications (
    id VARCHAR(255) PRIMARY KEY,
    type VARCHAR(50) NOT NULL DEFAULT 'POSTER_SUBMITTED',
    poster_id VARCHAR(255),
    user_id VARCHAR(255),
    health_center_name VARCHAR(200),
    message VARCHAR(1000),
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_notifications_read ON notifications(read);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_poster_id ON notifications(poster_id);

-- Add comments
COMMENT ON TABLE notifications IS 'إشعارات المدير | Admin notifications';
COMMENT ON COLUMN notifications.type IS 'نوع الإشعار: POSTER_SUBMITTED, POSTER_APPROVED | Notification type';
COMMENT ON COLUMN notifications.read IS 'هل تم قراءة الإشعار | Whether notification has been read';

