package com.in10nt.ems.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_comments")
@Data
public class TaskComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
    private String commentText;
    
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}