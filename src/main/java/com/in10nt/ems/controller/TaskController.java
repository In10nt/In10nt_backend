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
    
    @GetMapping("/created/{userId}")
    public List<Task> getTasksByCreatedUser(@PathVariable Long userId) {
        return taskRepository.findByCreatedById(userId);
    }
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> taskData,
                                         @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        try {
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
                @SuppressWarnings("unchecked")
                Map<String, Object> assignedToMap = (Map<String, Object>) assignedToData;
                if (assignedToMap.get("id") != null) {
                    Long userId = Long.valueOf(assignedToMap.get("id").toString());
                    User user = userRepository.findById(userId).orElse(null);
                    task.setAssignedTo(user);
                }
            }
            
            // Handle createdBy - automatically set from current user
            if (userIdHeader != null) {
                Long currentUserId = Long.valueOf(userIdHeader);
                User currentUser = userRepository.findById(currentUserId).orElse(null);
                if (currentUser != null) {
                    task.setCreatedBy(currentUser);
                }
            } else {
                // Fallback: check if createdBy is provided in request data
                Object createdByData = taskData.get("createdBy");
                if (createdByData instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> createdByMap = (Map<String, Object>) createdByData;
                    if (createdByMap.get("id") != null) {
                        Long userId = Long.valueOf(createdByMap.get("id").toString());
                        User user = userRepository.findById(userId).orElse(null);
                        task.setCreatedBy(user);
                    }
                }
            }
            
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            task.setProgress(0);
            
            Task savedTask = taskRepository.save(task);
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> taskData, 
                                       @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        try {
            return taskRepository.findById(id)
                    .map(task -> {
                        // Check permissions - only admin or task creator can update
                        if (userIdHeader != null) {
                            Long currentUserId = Long.valueOf(userIdHeader);
                            User currentUser = userRepository.findById(currentUserId).orElse(null);
                            
                            // If not admin and not the creator, deny access
                            if (currentUser != null && 
                                !currentUser.getRole().equals(User.Role.ADMIN) && 
                                !currentUser.getRole().equals(User.Role.CEO) &&
                                task.getCreatedBy() != null &&
                                !task.getCreatedBy().getId().equals(currentUserId)) {
                                return ResponseEntity.status(403).body("You can only update tasks you created");
                            }
                        }
                        
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
                                @SuppressWarnings("unchecked")
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
                        return ResponseEntity.ok(savedTask);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
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
                        return ResponseEntity.ok(savedTask);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
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
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, 
                                       @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        return taskRepository.findById(id)
                .map(task -> {
                    // Check permissions - only admin or task creator can delete
                    if (userIdHeader != null) {
                        Long currentUserId = Long.valueOf(userIdHeader);
                        User currentUser = userRepository.findById(currentUserId).orElse(null);
                        
                        // If not admin and not the creator, deny access
                        if (currentUser != null && 
                            !currentUser.getRole().equals(User.Role.ADMIN) && 
                            !currentUser.getRole().equals(User.Role.CEO) &&
                            task.getCreatedBy() != null &&
                            !task.getCreatedBy().getId().equals(currentUserId)) {
                            return ResponseEntity.status(403).body("You can only delete tasks you created");
                        }
                    }
                    
                    taskRepository.delete(task);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}