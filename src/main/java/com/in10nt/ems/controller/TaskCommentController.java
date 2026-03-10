package com.in10nt.ems.controller;

import com.in10nt.ems.model.TaskComment;
import com.in10nt.ems.model.Task;
import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.TaskCommentRepository;
import com.in10nt.ems.repository.TaskRepository;
import com.in10nt.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task-comments")
@RequiredArgsConstructor
public class TaskCommentController {
    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    @GetMapping("/task/{taskId}")
    public List<TaskComment> getCommentsByTaskId(@PathVariable Long taskId) {
        return taskCommentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }
    
    @PostMapping
    public ResponseEntity<TaskComment> createComment(@RequestBody Map<String, Object> commentData) {
        try {
            Long taskId = Long.valueOf(commentData.get("taskId").toString());
            Long createdById = Long.valueOf(commentData.get("createdById").toString());
            
            Task task = taskRepository.findById(taskId).orElse(null);
            User createdBy = userRepository.findById(createdById).orElse(null);
            
            if (task == null || createdBy == null) {
                return ResponseEntity.badRequest().build();
            }
            
            TaskComment comment = new TaskComment();
            comment.setTask(task);
            comment.setCommentText((String) commentData.get("commentText"));
            comment.setCreatedBy(createdBy);
            comment.setCreatedAt(LocalDateTime.now());
            
            TaskComment savedComment = taskCommentRepository.save(comment);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            System.err.println("Error creating comment: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        return taskCommentRepository.findById(id)
                .map(comment -> {
                    taskCommentRepository.delete(comment);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}