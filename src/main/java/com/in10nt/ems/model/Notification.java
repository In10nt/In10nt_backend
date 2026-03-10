package com.in10nt.ems.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "tasks", "createdTasks"})
    private User user; // Who should receive this notification
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_user_id")
    @JsonIgnoreProperties({"password", "tasks", "createdTasks"})
    private User fromUser; // Who sent/triggered this notification
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @Column(name = "reference_id")
    private Long referenceId; // ID of related entity (task, attachment, etc.)
    
    @Column(name = "reference_type")
    private String referenceType; // Type of related entity (TASK, ATTACHMENT, COMMENT, etc.)
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime readAt;
    
    public enum NotificationType {
        TASK_ASSIGNED,           // Task assigned to employee
        TASK_COMMENT,            // New comment on task
        TASK_REMINDER,           // Task reminder sent
        ATTACHMENT_COMMENT,      // New comment on attachment
        ATTACHMENT_APPROVED,     // Attachment approved
        ATTACHMENT_REJECTED,     // Attachment rejected
        ATTACHMENT_REVERTED,     // Attachment status reverted
        SUBTASK_COMPLETED,       // Subtask completed
        TASK_COMPLETED,          // Task completed
        GENERAL                  // General notification
    }
}