package com.in10nt.ems.controller;

import com.in10nt.ems.model.Task;
import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.TaskRepository;
import com.in10nt.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
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
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> taskData) {
        try {
            Task task = new Task();
            task.setTitle((String) taskData.get("title"));
            task.setDescription((String) taskData.get("description"));
            task.setPriority(Task.Priority.valueOf((String) taskData.get("priority")));
            task.setStatus(Task.Status.valueOf((String) taskData.get("status")));
            
            // Handle dueDate
            String dueDateStr = (String) taskData.get("dueDate");
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                task.setDueDate(LocalDateTime.parse(dueDateStr + "T00:00:00"));
            }
            
            // Handle assignedTo
            Map<String, Object> assignedToData = (Map<String, Object>) taskData.get("assignedTo");
            if (assignedToData != null && assignedToData.get("id") != null) {
                Long userId = Long.valueOf(assignedToData.get("id").toString());
                User user = userRepository.findById(userId).orElse(null);
                task.setAssignedTo(user);
            }
            
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            
            Task savedTask = taskRepository.save(task);
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            System.err.println("Error creating task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> taskData) {
        try {
            return taskRepository.findById(id)
                    .map(task -> {
                        System.out.println("Updating task with ID: " + id);
                        System.out.println("Task data received: " + taskData);
                        
                        task.setTitle((String) taskData.get("title"));
                        task.setDescription((String) taskData.get("description"));
                        task.setPriority(Task.Priority.valueOf((String) taskData.get("priority")));
                        
                        // Only update status if provided
                        if (taskData.get("status") != null) {
                            task.setStatus(Task.Status.valueOf((String) taskData.get("status")));
                        }
                        
                        // Handle dueDate
                        String dueDateStr = (String) taskData.get("dueDate");
                        if (dueDateStr != null && !dueDateStr.isEmpty()) {
                            task.setDueDate(LocalDateTime.parse(dueDateStr + "T00:00:00"));
                        } else {
                            task.setDueDate(null);
                        }
                        
                        // Handle assignedTo
                        Map<String, Object> assignedToData = (Map<String, Object>) taskData.get("assignedTo");
                        if (assignedToData != null && assignedToData.get("id") != null) {
                            Long userId = Long.valueOf(assignedToData.get("id").toString());
                            User user = userRepository.findById(userId).orElse(null);
                            task.setAssignedTo(user);
                        } else {
                            task.setAssignedTo(null);
                        }
                        
                        task.setUpdatedAt(LocalDateTime.now());
                        
                        Task savedTask = taskRepository.save(task);
                        System.out.println("Task updated successfully: " + savedTask.getId());
                        return ResponseEntity.ok(savedTask);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error updating task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
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
