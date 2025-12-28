package com.kirkukhealth.poster.repository;

import com.kirkukhealth.poster.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Message operations
 * مستودع عمليات الرسائل
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    
    /**
     * Find conversation between two users
     * البحث عن المحادثة بين مستخدمين
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("userId1") String userId1, @Param("userId2") String userId2);
    
    /**
     * Find unread messages for a user
     * البحث عن الرسائل غير المقروءة لمستخدم
     */
    List<Message> findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(String receiverId);
    
    /**
     * Find messages related to a poster
     * البحث عن الرسائل المتعلقة ببوستر
     */
    List<Message> findByPosterIdOrderByCreatedAtAsc(String posterId);
    
    /**
     * Count unread messages for a user
     * عدد الرسائل غير المقروءة لمستخدم
     */
    long countByReceiverIdAndIsReadFalse(String receiverId);
}

