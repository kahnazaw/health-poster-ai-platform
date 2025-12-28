package com.kirkukhealth.poster.service;

import com.kirkukhealth.poster.model.*;
import com.kirkukhealth.poster.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Communication Service
 * خدمة الاتصالات
 * 
 * Manages:
 * - Official Bulletins (MOH directives)
 * - Private Chat (Admin ↔ Centers)
 * - Knowledge Base (FAQ)
 * - File Sharing
 * - Poster Comments
 */
@Service
public class CommunicationService {

    @Autowired
    private BulletinRepository bulletinRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FAQRepository faqRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private PosterCommentRepository posterCommentRepository;

    // ============================================================================
    // BULLETIN METHODS
    // ============================================================================

    /**
     * Create bulletin (Admin only)
     * إنشاء نشرة (فقط المدير)
     */
    @Transactional
    public Bulletin createBulletin(Bulletin bulletin) {
        return bulletinRepository.save(bulletin);
    }

    /**
     * Get all bulletins (ordered by pinned first, then date)
     * الحصول على جميع النشرات
     */
    public List<Bulletin> getAllBulletins() {
        return bulletinRepository.findAllByOrderByIsPinnedDescCreatedAtDesc();
    }

    /**
     * Get pinned bulletins
     * الحصول على النشرات المثبتة
     */
    public List<Bulletin> getPinnedBulletins() {
        return bulletinRepository.findByIsPinnedTrueOrderByCreatedAtDesc();
    }

    // ============================================================================
    // MESSAGE METHODS
    // ============================================================================

    /**
     * Send message
     * إرسال رسالة
     */
    @Transactional
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    /**
     * Get conversation between two users
     * الحصول على المحادثة بين مستخدمين
     */
    public List<Message> getConversation(String userId1, String userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }

    /**
     * Get unread messages for a user
     * الحصول على الرسائل غير المقروءة لمستخدم
     */
    public List<Message> getUnreadMessages(String userId) {
        return messageRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * Mark message as read
     * تحديد الرسالة كمقروءة
     */
    @Transactional
    public void markAsRead(String messageId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setIsRead(true);
            messageRepository.save(message);
        });
    }

    /**
     * Get unread count for a user
     * الحصول على عدد الرسائل غير المقروءة
     */
    public long getUnreadCount(String userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    // ============================================================================
    // FAQ METHODS
    // ============================================================================

    /**
     * Create FAQ entry
     * إنشاء إدخال FAQ
     */
    @Transactional
    public FAQEntry createFAQ(FAQEntry faq) {
        return faqRepository.save(faq);
    }

    /**
     * Get all FAQ entries
     * الحصول على جميع إدخالات FAQ
     */
    public List<FAQEntry> getAllFAQs() {
        return faqRepository.findAll();
    }

    /**
     * Get featured FAQs
     * الحصول على FAQs المميزة
     */
    public List<FAQEntry> getFeaturedFAQs() {
        return faqRepository.findByIsFeaturedTrueOrderByViewCountDesc();
    }

    /**
     * Search FAQs
     * البحث في FAQs
     */
    public List<FAQEntry> searchFAQs(String query) {
        return faqRepository.search(query);
    }

    /**
     * Get FAQ by ID and increment view count
     * الحصول على FAQ بالمعرف وزيادة عدد المشاهدات
     */
    @Transactional
    public Optional<FAQEntry> getFAQById(String id) {
        Optional<FAQEntry> faq = faqRepository.findById(id);
        faq.ifPresent(entry -> {
            entry.setViewCount(entry.getViewCount() + 1);
            faqRepository.save(entry);
        });
        return faq;
    }

    /**
     * Get all categories
     * الحصول على جميع الفئات
     */
    public List<String> getFAQCategories() {
        return faqRepository.findAllCategories();
    }

    // ============================================================================
    // FILE UPLOAD METHODS
    // ============================================================================

    /**
     * Save file upload record
     * حفظ سجل رفع الملف
     */
    @Transactional
    public FileUpload saveFileUpload(FileUpload fileUpload) {
        return fileUploadRepository.save(fileUpload);
    }

    /**
     * Get all public files
     * الحصول على جميع الملفات العامة
     */
    public List<FileUpload> getPublicFiles() {
        return fileUploadRepository.findByIsPublicTrueOrderByCreatedAtDesc();
    }

    /**
     * Get files by category
     * الحصول على الملفات حسب الفئة
     */
    public List<FileUpload> getFilesByCategory(String category) {
        return fileUploadRepository.findByCategoryOrderByCreatedAtDesc(category);
    }

    /**
     * Increment download count
     * زيادة عدد التحميلات
     */
    @Transactional
    public void incrementDownloadCount(String fileId) {
        fileUploadRepository.findById(fileId).ifPresent(file -> {
            file.setDownloadCount(file.getDownloadCount() + 1);
            fileUploadRepository.save(file);
        });
    }

    // ============================================================================
    // POSTER COMMENT METHODS
    // ============================================================================

    /**
     * Add comment to poster
     * إضافة تعليق على بوستر
     */
    @Transactional
    public PosterComment addComment(PosterComment comment) {
        return posterCommentRepository.save(comment);
    }

    /**
     * Get comments for a poster
     * الحصول على التعليقات لبوستر
     */
    public List<PosterComment> getPosterComments(String posterId) {
        return posterCommentRepository.findByPosterIdOrderByCreatedAtAsc(posterId);
    }

    /**
     * Get admin comments for a poster
     * الحصول على تعليقات المدير لبوستر
     */
    public List<PosterComment> getAdminComments(String posterId) {
        return posterCommentRepository.findByPosterIdAndIsAdminCommentTrueOrderByCreatedAtAsc(posterId);
    }
}

