package com.in10nt.ems.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_reminders")
@Data
public class TaskReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @Column(name = "reminder_text", nullable = false, columnDefinition = "TEXT")
    private String reminderText;
    
    @ManyToOne
    @JoinColumn(name = "sent_by", nullable = false)
    private User sentBy;
    
    @ManyToOne
    @JoinColumn(name = "sent_to", nullable = false)
    private User sentTo;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}