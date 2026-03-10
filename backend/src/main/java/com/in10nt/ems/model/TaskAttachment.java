package com.in10nt.ems.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_attachments")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaskAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnoreProperties({"subtasks", "taskComments", "taskReminders", "attachments"})
    private Task task;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploaded_by", nullable = false)
    @JsonIgnoreProperties({"password", "tasks", "createdTasks"})
    private User uploadedBy;
    
    @Column(nullable = false)
    private String fileName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttachmentType type;
    
    @Column(columnDefinition = "TEXT")
    private String fileUrl; // For links or base64 images
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by")
    @JsonIgnoreProperties({"password", "tasks", "createdTasks"})
    private User reviewedBy;
    
    @Column(columnDefinition = "TEXT")
    private String reviewComment;
    
    private LocalDateTime reviewedAt;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum AttachmentType {
        LINK, IMAGE
    }
    
    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
}