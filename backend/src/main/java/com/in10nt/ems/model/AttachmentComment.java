package com.in10nt.ems.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "attachment_comments")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AttachmentComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attachment_id", nullable = false)
    @JsonIgnoreProperties({"task", "uploadedBy", "reviewedBy"})
    private TaskAttachment attachment;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commented_by", nullable = false)
    @JsonIgnoreProperties({"password", "tasks", "createdTasks"})
    private User commentedBy;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String commentText;
    
    @Enumerated(EnumType.STRING)
    private CommentType commentType = CommentType.COMMENT;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum CommentType {
        COMMENT,        // Regular comment
        STATUS_CHANGE,  // When status is changed
        REPLY          // Reply to another comment
    }
}