package com.in10nt.ems.repository;

import com.in10nt.ems.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Get all notifications for a user, ordered by newest first
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Get unread notifications for a user
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    // Count unread notifications for a user
    long countByUserIdAndIsReadFalse(Long userId);
    
    // Mark notification as read
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);
    
    // Mark all notifications as read for a user
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadForUser(@Param("userId") Long userId);
    
    // Delete old read notifications (older than 30 days)
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.isRead = true AND n.readAt < :cutoffDate")
    void deleteOldReadNotifications(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}