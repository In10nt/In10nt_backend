package com.in10nt.ems.controller;

import com.in10nt.ems.model.TaskReminder;
import com.in10nt.ems.model.Task;
import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.TaskReminderRepository;
import com.in10nt.ems.repository.TaskRepository;
import com.in10nt.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task-reminders")
@RequiredArgsConstructor
public class TaskReminderController {
    private final TaskReminderRepository taskReminderRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    @GetMapping("/task/{taskId}")
    public List<TaskReminder> getRemindersByTaskId(@PathVariable Long taskId) {
        return taskReminderRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }
    
    @GetMapping("/user/{userId}")
    public List<TaskReminder> getRemindersByUserId(@PathVariable Long userId) {
        return taskReminderRepository.findBySentToIdOrderByCreatedAtDesc(userId);
    }
    
    @GetMapping("/user/{userId}/unread")
    public List<TaskReminder> getUnreadRemindersByUserId(@PathVariable Long userId) {
        return taskReminderRepository.findBySentToIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    @PostMapping
    public ResponseEntity<TaskReminder> createReminder(@RequestBody Map<String, Object> reminderData) {
        try {
            Long taskId = Long.valueOf(reminderData.get("taskId").toString());
            Long sentById = Long.valueOf(reminderData.get("sentById").toString());
            Long sentToId = Long.valueOf(reminderData.get("sentToId").toString());
            
            Task task = taskRepository.findById(taskId).orElse(null);
            User sentBy = userRepository.findById(sentById).orElse(null);
            User sentTo = userRepository.findById(sentToId).orElse(null);
            
            if (task == null || sentBy == null || sentTo == null) {
                return ResponseEntity.badRequest().build();
            }
            
            TaskReminder reminder = new TaskReminder();
            reminder.setTask(task);
            reminder.setReminderText((String) reminderData.get("reminderText"));
            reminder.setSentBy(sentBy);
            reminder.setSentTo(sentTo);
            reminder.setIsRead(false);
            reminder.setCreatedAt(LocalDateTime.now());
            
            TaskReminder savedReminder = taskReminderRepository.save(reminder);
            return ResponseEntity.ok(savedReminder);
        } catch (Exception e) {
            System.err.println("Error creating reminder: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<TaskReminder> markReminderAsRead(@PathVariable Long id) {
        return taskReminderRepository.findById(id)
                .map(reminder -> {
                    reminder.setIsRead(true);
                    TaskReminder savedReminder = taskReminderRepository.save(reminder);
                    return ResponseEntity.ok(savedReminder);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable Long id) {
        return taskReminderRepository.findById(id)
                .map(reminder -> {
                    taskReminderRepository.delete(reminder);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}