-- ============================================================================
-- Database Migration V7: Create user cloud settings table
-- ============================================================================
-- إنشاء جدول إعدادات التخزين السحابي للمستخدمين
-- Create table for user cloud storage settings (Google Drive OAuth)
--
-- Stores encrypted OAuth tokens for individual manager Google Drive linking
-- يخزن رموز OAuth المشفرة لربط Google Drive لكل مدير بشكل فردي
-- ============================================================================

CREATE TABLE IF NOT EXISTS user_cloud_settings (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    google_drive_enabled BOOLEAN DEFAULT FALSE,
    access_token_encrypted TEXT,
    refresh_token_encrypted TEXT,
    token_expires_at TIMESTAMP,
    google_drive_folder_id VARCHAR(500),
    google_drive_folder_name VARCHAR(500) DEFAULT 'My Health Reports - Sector 1',
    last_sync_at TIMESTAMP,
    sync_status VARCHAR(50) DEFAULT 'NOT_LINKED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Link to user_profiles
    CONSTRAINT fk_user_cloud_settings_user FOREIGN KEY (user_id) 
        REFERENCES user_profiles(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_cloud_settings_user_id ON user_cloud_settings(user_id);
CREATE INDEX IF NOT EXISTS idx_user_cloud_settings_sync_status ON user_cloud_settings(sync_status);

COMMENT ON TABLE user_cloud_settings IS 'إعدادات التخزين السحابي للمستخدمين | User cloud storage settings';
COMMENT ON COLUMN user_cloud_settings.user_id IS 'معرف المستخدم (مرتبط بـ user_profiles) | User ID (linked to user_profiles)';
COMMENT ON COLUMN user_cloud_settings.access_token_encrypted IS 'رمز الوصول المشفر | Encrypted access token';
COMMENT ON COLUMN user_cloud_settings.refresh_token_encrypted IS 'رمز التحديث المشفر | Encrypted refresh token';
COMMENT ON COLUMN user_cloud_settings.sync_status IS 'حالة المزامنة: NOT_LINKED, LINKED, SYNCING, SYNCED, ERROR | Sync status';
COMMENT ON COLUMN user_cloud_settings.google_drive_folder_id IS 'معرف مجلد Google Drive | Google Drive folder ID';

