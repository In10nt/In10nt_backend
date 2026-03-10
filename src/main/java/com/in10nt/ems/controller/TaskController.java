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
            System.out.println("Creating task with data: " + taskData);
            
            Task task = new Task();
            task.setTitle((String) taskData.get("title"));
            task.setDescription((String) taskData.get("description"));
            
            // Handle priority
            String priority = (String) taskData.get("priority");
            if (priority != null) {
                task.setPriority(Task.Priority.valueOf(priority));
            }
            
            // Handle status
            String status = (String) taskData.get("status");
            if (status != null) {
                task.setStatus(Task.Status.valueOf(status));
            } else {
                task.setStatus(Task.Status.PENDING);
            }
            
            // Handle dueDate
            String dueDateStr = (String) taskData.get("dueDate");
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                task.setDueDate(LocalDateTime.parse(dueDateStr + "T00:00:00"));
            }
            
            // Handle assignedTo
            Object assignedToData = taskData.get("assignedTo");
            if (assignedToData instanceof Map) {
                Map<String, Object> assignedToMap = (Map<String, Object>) assignedToData;
                if (assignedToMap.get("id") != null) {
                    Long userId = Long.valueOf(assignedToMap.get("id").toString());
                    User user = userRepository.findById(userId).orElse(null);
                    task.setAssignedTo(user);
                }
            }
            
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            task.setProgress(0);
            
            Task savedTask = taskRepository.save(task);
            System.out.println("Task created successfully with ID: " + savedTask.getId());
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
                        
                        if (taskData.get("title") != null) {
                            task.setTitle((String) taskData.get("title"));
                        }
                        if (taskData.get("description") != null) {
                            task.setDescription((String) taskData.get("description"));
                        }
                        if (taskData.get("priority") != null) {
                            task.setPriority(Task.Priority.valueOf((String) taskData.get("priority")));
                        }
                        
                        // Only update status if provided
                        if (taskData.get("status") != null) {
                            task.setStatus(Task.Status.valueOf((String) taskData.get("status")));
                        }
                        
                        // Handle dueDate
                        if (taskData.containsKey("dueDate")) {
                            String dueDateStr = (String) taskData.get("dueDate");
                            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                                task.setDueDate(LocalDateTime.parse(dueDateStr + "T00:00:00"));
                            } else {
                                task.setDueDate(null);
                            }
                        }
                        
                        // Handle assignedTo
                        if (taskData.containsKey("assignedTo")) {
                            Object assignedToData = taskData.get("assignedTo");
                            if (assignedToData instanceof Map) {
                                Map<String, Object> assignedToMap = (Map<String, Object>) assignedToData;
                                if (assignedToMap.get("id") != null) {
                                    Long userId = Long.valueOf(assignedToMap.get("id").toString());
                                    User user = userRepository.findById(userId).orElse(null);
                                    task.setAssignedTo(user);
                                }
                            } else {
                                task.setAssignedTo(null);
                            }
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
    
    @PutMapping("/{id}/start")
    public ResponseEntity<Task> startTask(@PathVariable Long id) {
        try {
            return taskRepository.findById(id)
                    .map(task -> {
                        task.setStatus(Task.Status.IN_PROGRESS);
                        task.setTaskStartedAt(LocalDateTime.now());
                        task.setUpdatedAt(LocalDateTime.now());
                        
                        Task savedTask = taskRepository.save(task);
                        System.out.println("Task started: " + savedTask.getId());
                        return ResponseEntity.ok(savedTask);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error starting task: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/with-details")
    public ResponseEntity<Map<String, Object>> getTaskWithDetails(@PathVariable Long id) {
        try {
            return taskRepository.findById(id)
                    .map(task -> {
                        Map<String, Object> taskDetails = new java.util.HashMap<>();
                        taskDetails.put("task", task);
                        taskDetails.put("progress", task.calculateProgress());
                        return ResponseEntity.ok(taskDetails);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error getting task details: " + e.getMessage());
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