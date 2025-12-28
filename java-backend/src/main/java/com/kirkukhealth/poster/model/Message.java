package com.kirkukhealth.poster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Message Entity (Private Chat)
 * كيان الرسالة (دردشة خاصة)
 * 
 * One-on-one secure chat between Admin and each Health Center
 * دردشة آمنة واحد لواحد بين المدير وكل مركز صحي
 */
@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Sender ID (Admin or Center Manager)
     * معرف المرسل (المدير أو مدير المركز)
     */
    @Column(name = "sender_id", nullable = false)
    private String senderId;

    /**
     * Receiver ID (Admin or Center Manager)
     * معرف المستقبل (المدير أو مدير المركز)
     */
    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    /**
     * Message Content
     * محتوى الرسالة
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Whether message has been read
     * هل تمت قراءة الرسالة
     */
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    /**
     * Poster ID (if message is about a specific poster)
     * معرف البوستر (إذا كانت الرسالة متعلقة ببوستر محدد)
     */
    @Column(name = "poster_id")
    private String posterId;

    /**
     * Created timestamp
     * وقت الإنشاء
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }
}

