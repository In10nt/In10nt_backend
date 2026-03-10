package com.in10nt.ems.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to")
    @JsonIgnoreProperties({"password", "tasks", "createdTasks"})
    private User assignedTo;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"password", "tasks", "createdTasks"})
    private User createdBy;
    
    private LocalDateTime dueDate;
    private Integer progress = 0;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "task_started_at")
    private LocalDateTime taskStartedAt;
    
    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"parentTask"})
    private List<Subtask> subtasks;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"task"})
    private List<TaskComment> taskComments;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"task"})
    private List<TaskReminder> taskReminders;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"task"})
    private List<TaskAttachment> attachments;
    
    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED, APPROVED
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
    
    // Calculate progress based on completed subtasks
    public int calculateProgress() {
        if (subtasks == null || subtasks.isEmpty()) {
            return progress != null ? progress : 0;
        }
        
        long completedSubtasks = subtasks.stream()
                .mapToLong(subtask -> subtask.getIsCompleted() ? 1 : 0)
                .sum();
        
        return (int) ((completedSubtasks * 100) / subtasks.size());
    }
}
