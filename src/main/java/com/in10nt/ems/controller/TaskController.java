package com.in10nt.ems.controller;

import com.in10nt.ems.model.Task;
import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.TaskRepository;
import com.in10nt.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/assigned/{userId}")
    public List<Task> getTasksByAssignedUser(@PathVariable Long userId) {
        return taskRepository.findByAssignedToId(userId);
    }
    
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            System.out.println("Received task: " + task); // Debug log
            
            // Handle assignedTo if it has an ID
            if (task.getAssignedTo() != null && task.getAssignedTo().getId() != null) {
                User user = userRepository.findById(task.getAssignedTo().getId())
                        .orElse(null);
                task.setAssignedTo(user);
            }
            
            // Set default status if not provided
            if (task.getStatus() == null) {
                task.setStatus(Task.Status.PENDING);
            }
            
            // Set default progress if not provided
            if (task.getProgress() == null) {
                task.setProgress(0);
            }
            
            // Don't set createdAt/updatedAt manually - let @PrePersist handle it
            task.setCreatedAt(null);
            task.setUpdatedAt(null);
            
            Task savedTask = taskRepository.save(task);
            System.out.println("Saved task: " + savedTask); // Debug log
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace
            return ResponseEntity.badRequest().body("Error creating task: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(taskDetails.getTitle());
                    task.setDescription(taskDetails.getDescription());
                    task.setStatus(taskDetails.getStatus());
                    task.setPriority(taskDetails.getPriority());
                    task.setAssignedTo(taskDetails.getAssignedTo());
                    task.setDueDate(taskDetails.getDueDate());
                    task.setProgress(taskDetails.getProgress());
                    task.setComments(taskDetails.getComments());
                    task.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(taskRepository.save(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
