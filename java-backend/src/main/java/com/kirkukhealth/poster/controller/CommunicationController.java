package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.model.*;
import com.kirkukhealth.poster.service.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Communication Controller (REST API)
 * متحكم الاتصالات (REST API)
 * 
 * Handles:
 * - Official Bulletins
 * - Private Chat Messages
 * - Knowledge Base (FAQ)
 * - File Sharing
 * - Poster Comments
 */
@RestController
@RequestMapping("/api/communication")
@CrossOrigin(origins = "*")
public class CommunicationController {

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private com.kirkukhealth.poster.repository.FileUploadRepository fileUploadRepository;

    private static final String UPLOAD_DIR = "uploads/files/";

    static {
        // Create upload directory on startup
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to create upload directory: " + e.getMessage());
        }
    }

    // ============================================================================
    // BULLETIN ENDPOINTS
    // ============================================================================

    /**
     * Create bulletin (Admin only)
     * إنشاء نشرة (فقط المدير)
     * 
     * POST /api/communication/bulletins
     */
    @PostMapping("/bulletins")
    public ResponseEntity<Map<String, Object>> createBulletin(@RequestBody BulletinRequest request) {
        try {
            Bulletin bulletin = Bulletin.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postedBy(request.getPostedBy())
                .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
                .isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
                .build();

            Bulletin saved = communicationService.createBulletin(bulletin);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "تم إنشاء النشرة بنجاح");
            response.put("bulletin", saved);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في إنشاء النشرة");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get all bulletins
     * الحصول على جميع النشرات
     * 
     * GET /api/communication/bulletins
     */
    @GetMapping("/bulletins")
    public ResponseEntity<Map<String, Object>> getAllBulletins() {
        List<Bulletin> bulletins = communicationService.getAllBulletins();
        
        Map<String, Object> response = new HashMap<>();
        response.put("bulletins", bulletins);
        response.put("count", bulletins.size());
        
        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // MESSAGE ENDPOINTS
    // ============================================================================

    /**
     * Send message
     * إرسال رسالة
     * 
     * POST /api/communication/messages
     */
    @PostMapping("/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody MessageRequest request) {
        try {
            Message message = Message.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .posterId(request.getPosterId())
                .isRead(false)
                .build();

            Message saved = communicationService.sendMessage(message);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "تم إرسال الرسالة بنجاح");
            response.put("savedMessage", saved);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في إرسال الرسالة");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get conversation
     * الحصول على المحادثة
     * 
     * GET /api/communication/messages/conversation?userId1={id1}&userId2={id2}
     */
    @GetMapping("/messages/conversation")
    public ResponseEntity<Map<String, Object>> getConversation(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        
        List<Message> messages = communicationService.getConversation(userId1, userId2);
        
        Map<String, Object> response = new HashMap<>();
        response.put("messages", messages);
        response.put("count", messages.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get unread messages
     * الحصول على الرسائل غير المقروءة
     * 
     * GET /api/communication/messages/unread?userId={userId}
     */
    @GetMapping("/messages/unread")
    public ResponseEntity<Map<String, Object>> getUnreadMessages(@RequestParam String userId) {
        List<Message> messages = communicationService.getUnreadMessages(userId);
        long count = communicationService.getUnreadCount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("messages", messages);
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark message as read
     * تحديد الرسالة كمقروءة
     * 
     * PUT /api/communication/messages/{messageId}/read
     */
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable String messageId) {
        communicationService.markAsRead(messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "تم تحديد الرسالة كمقروءة");
        
        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // FAQ ENDPOINTS
    // ============================================================================

    /**
     * Create FAQ entry
     * إنشاء إدخال FAQ
     * 
     * POST /api/communication/faq
     */
    @PostMapping("/faq")
    public ResponseEntity<Map<String, Object>> createFAQ(@RequestBody FAQRequest request) {
        try {
            FAQEntry faq = FAQEntry.builder()
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .category(request.getCategory())
                .tags(request.getTags())
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .build();

            FAQEntry saved = communicationService.createFAQ(faq);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "تم إنشاء السؤال الشائع بنجاح");
            response.put("faq", saved);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في إنشاء السؤال الشائع");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get all FAQs
     * الحصول على جميع FAQs
     * 
     * GET /api/communication/faq
     */
    @GetMapping("/faq")
    public ResponseEntity<Map<String, Object>> getAllFAQs() {
        List<FAQEntry> faqs = communicationService.getAllFAQs();
        
        Map<String, Object> response = new HashMap<>();
        response.put("faqs", faqs);
        response.put("count", faqs.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search FAQs
     * البحث في FAQs
     * 
     * GET /api/communication/faq/search?query={query}
     */
    @GetMapping("/faq/search")
    public ResponseEntity<Map<String, Object>> searchFAQs(@RequestParam String query) {
        List<FAQEntry> faqs = communicationService.searchFAQs(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("faqs", faqs);
        response.put("count", faqs.size());
        response.put("query", query);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get FAQ by ID
     * الحصول على FAQ بالمعرف
     * 
     * GET /api/communication/faq/{id}
     */
    @GetMapping("/faq/{id}")
    public ResponseEntity<Map<String, Object>> getFAQ(@PathVariable String id) {
        return communicationService.getFAQById(id)
            .map(faq -> {
                Map<String, Object> response = new HashMap<>();
                response.put("faq", faq);
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // ============================================================================
    // FILE UPLOAD ENDPOINTS
    // ============================================================================

    /**
     * Upload file
     * رفع ملف
     * 
     * POST /api/communication/files/upload
     */
    @PostMapping("/files/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadedBy") String uploadedBy,
            @RequestParam(value = "category", defaultValue = "GENERAL") String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", defaultValue = "true") Boolean isPublic) {
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique file name
            String originalName = file.getOriginalFilename();
            String extension = originalName != null && originalName.contains(".") 
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(fileName);

            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save file record
            FileUpload fileUpload = FileUpload.builder()
                .fileName(fileName)
                .originalName(originalName != null ? originalName : "unknown")
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .uploadedBy(uploadedBy)
                .category(category)
                .description(description)
                .isPublic(isPublic)
                .downloadCount(0)
                .build();

            FileUpload saved = communicationService.saveFileUpload(fileUpload);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "تم رفع الملف بنجاح");
            response.put("file", saved);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في رفع الملف");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get all public files
     * الحصول على جميع الملفات العامة
     * 
     * GET /api/communication/files
     */
    @GetMapping("/files")
    public ResponseEntity<Map<String, Object>> getPublicFiles() {
        List<FileUpload> files = communicationService.getPublicFiles();
        
        Map<String, Object> response = new HashMap<>();
        response.put("files", files);
        response.put("count", files.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Download file
     * تحميل ملف
     * 
     * GET /api/communication/files/{fileId}/download
     */
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<?> downloadFile(@PathVariable String fileId) {
        try {
            Optional<FileUpload> fileOpt = fileUploadRepository.findById(fileId);
            if (fileOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            FileUpload file = fileOpt.get();
            Path filePath = Paths.get(file.getFilePath());

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // Increment download count
            communicationService.incrementDownloadCount(fileId);

            // Return file
            byte[] fileBytes = Files.readAllBytes(filePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(file.getMimeType() != null ? file.getMimeType() : "application/octet-stream"));
            headers.setContentDispositionFormData("attachment", file.getOriginalName());
            headers.setContentLength(fileBytes.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ============================================================================
    // POSTER COMMENT ENDPOINTS
    // ============================================================================

    /**
     * Add comment to poster
     * إضافة تعليق على بوستر
     * 
     * POST /api/communication/posters/{posterId}/comments
     */
    @PostMapping("/posters/{posterId}/comments")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable String posterId,
            @RequestBody CommentRequest request) {
        
        try {
            PosterComment comment = PosterComment.builder()
                .posterId(posterId)
                .commenterId(request.getCommenterId())
                .content(request.getContent())
                .isAdminComment(request.getIsAdminComment() != null ? request.getIsAdminComment() : false)
                .build();

            PosterComment saved = communicationService.addComment(comment);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("message", "تم إضافة التعليق بنجاح");
            response.put("comment", saved);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "خطأ في إضافة التعليق");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get comments for a poster
     * الحصول على التعليقات لبوستر
     * 
     * GET /api/communication/posters/{posterId}/comments
     */
    @GetMapping("/posters/{posterId}/comments")
    public ResponseEntity<Map<String, Object>> getPosterComments(@PathVariable String posterId) {
        List<PosterComment> comments = communicationService.getPosterComments(posterId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("comments", comments);
        response.put("count", comments.size());
        
        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // REQUEST DTOs
    // ============================================================================

    public static class BulletinRequest {
        private String title;
        private String content;
        private String postedBy;
        private String priority;
        private Boolean isPinned;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getPostedBy() { return postedBy; }
        public void setPostedBy(String postedBy) { this.postedBy = postedBy; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public Boolean getIsPinned() { return isPinned; }
        public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }
    }

    public static class MessageRequest {
        private String senderId;
        private String receiverId;
        private String content;
        private String posterId;

        // Getters and Setters
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getReceiverId() { return receiverId; }
        public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getPosterId() { return posterId; }
        public void setPosterId(String posterId) { this.posterId = posterId; }
    }

    public static class FAQRequest {
        private String question;
        private String answer;
        private String category;
        private String tags;
        private Boolean isFeatured;

        // Getters and Setters
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
        public Boolean getIsFeatured() { return isFeatured; }
        public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    }

    public static class CommentRequest {
        private String commenterId;
        private String content;
        private Boolean isAdminComment;

        // Getters and Setters
        public String getCommenterId() { return commenterId; }
        public void setCommenterId(String commenterId) { this.commenterId = commenterId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Boolean getIsAdminComment() { return isAdminComment; }
        public void setIsAdminComment(Boolean isAdminComment) { this.isAdminComment = isAdminComment; }
    }
}

