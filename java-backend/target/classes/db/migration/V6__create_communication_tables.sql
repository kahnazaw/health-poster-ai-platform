-- ============================================================================
-- Database Migration V6: Create communication center tables
-- ============================================================================
-- إنشاء جداول مركز الاتصالات الموحد
-- Create tables for unified communication center
--
-- Tables:
-- - bulletins: Official MOH directives
-- - messages: Private chat messages
-- - faq_entries: Knowledge base Q&A
-- - file_uploads: Shared files (logos, documents)
-- - poster_comments: Comments on posters (linked to approval workflow)
-- ============================================================================

-- ============================================================================
-- Bulletins Table (Official MOH Directives)
-- ============================================================================
CREATE TABLE IF NOT EXISTS bulletins (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    posted_by VARCHAR(255) NOT NULL,
    priority VARCHAR(20) DEFAULT 'NORMAL',
    is_pinned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_bulletins_created_at ON bulletins(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_bulletins_priority ON bulletins(priority);

COMMENT ON TABLE bulletins IS 'التعليمات الرسمية من وزارة الصحة | Official MOH directives';
COMMENT ON COLUMN bulletins.posted_by IS 'معرف المدير الذي نشر التعليمات | Admin ID who posted';

-- ============================================================================
-- Messages Table (Private Chat)
-- ============================================================================
CREATE TABLE IF NOT EXISTS messages (
    id VARCHAR(255) PRIMARY KEY,
    sender_id VARCHAR(255) NOT NULL,
    receiver_id VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    poster_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_receiver ON messages(receiver_id);
CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(sender_id, receiver_id);
CREATE INDEX IF NOT EXISTS idx_messages_poster ON messages(poster_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at DESC);

COMMENT ON TABLE messages IS 'رسائل الدردشة الخاصة | Private chat messages';
COMMENT ON COLUMN messages.poster_id IS 'معرف البوستر (إذا كانت الرسالة متعلقة ببوستر) | Poster ID if message is about a poster';

-- ============================================================================
-- FAQ Entries Table (Knowledge Base)
-- ============================================================================
CREATE TABLE IF NOT EXISTS faq_entries (
    id VARCHAR(255) PRIMARY KEY,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    category VARCHAR(200),
    tags VARCHAR(500),
    view_count INTEGER DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_faq_category ON faq_entries(category);
CREATE INDEX IF NOT EXISTS idx_faq_featured ON faq_entries(is_featured);
CREATE INDEX IF NOT EXISTS idx_faq_search ON faq_entries USING gin(to_tsvector('arabic', question || ' ' || answer));

COMMENT ON TABLE faq_entries IS 'قاعدة المعرفة (أسئلة وأجوبة) | Knowledge base Q&A';
COMMENT ON COLUMN faq_entries.tags IS 'علامات للبحث (مفصولة بفواصل) | Search tags (comma-separated)';

-- ============================================================================
-- File Uploads Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS file_uploads (
    id VARCHAR(255) PRIMARY KEY,
    file_name VARCHAR(500) NOT NULL,
    original_name VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100),
    uploaded_by VARCHAR(255) NOT NULL,
    category VARCHAR(100) DEFAULT 'GENERAL',
    description TEXT,
    is_public BOOLEAN DEFAULT TRUE,
    download_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_file_uploads_category ON file_uploads(category);
CREATE INDEX IF NOT EXISTS idx_file_uploads_uploaded_by ON file_uploads(uploaded_by);
CREATE INDEX IF NOT EXISTS idx_file_uploads_created_at ON file_uploads(created_at DESC);

COMMENT ON TABLE file_uploads IS 'الملفات المشتركة | Shared files';
COMMENT ON COLUMN file_uploads.category IS 'الفئة: LOGO, DOCUMENT, TEMPLATE, OTHER | Category';

-- ============================================================================
-- Poster Comments Table (Linked to Approval Workflow)
-- ============================================================================
CREATE TABLE IF NOT EXISTS poster_comments (
    id VARCHAR(255) PRIMARY KEY,
    poster_id VARCHAR(255) NOT NULL,
    commenter_id VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_admin_comment BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_poster_comments_poster ON poster_comments(poster_id);
CREATE INDEX IF NOT EXISTS idx_poster_comments_created_at ON poster_comments(created_at DESC);

COMMENT ON TABLE poster_comments IS 'تعليقات على البوسترات (مرتبطة بسير عمل الموافقة) | Comments on posters (linked to approval workflow)';

