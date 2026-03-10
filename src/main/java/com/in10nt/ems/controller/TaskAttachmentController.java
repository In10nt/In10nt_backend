package com.in10nt.ems.controller;

import com.in10nt.ems.model.TaskAttachment;
import com.in10nt.ems.model.Task;
import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.TaskAttachmentRepository;
import com.in10nt.ems.repository.TaskRepository;
import com.in10nt.ems.repository.UserRepository;
import com.in10nt.ems.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task-attachments")
@RequiredArgsConstructor
public class TaskAttachmentController {
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("TaskAttachmentController is working!");
    }
    
    @GetMapping("/task/{taskId}")
    public List<TaskAttachment> getAttachmentsByTaskId(@PathVariable Long taskId) {
        return taskAttachmentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }
    
    @GetMapping("/user/{userId}")
    public List<TaskAttachment> getAttachmentsByUserId(@PathVariable Long userId) {
        return taskAttachmentRepository.findByUploadedByIdOrderByCreatedAtDesc(userId);
    }
    
    @GetMapping("/pending")
    public List<TaskAttachment> getPendingAttachments() {
        return taskAttachmentRepository.findByApprovalStatusOrderByCreatedAtDesc(TaskAttachment.ApprovalStatus.PENDING);
    }
    
    @PostMapping
    public ResponseEntity<?> createAttachment(@RequestBody Map<String, Object> attachmentData) {
        try {
            System.out.println("Received attachment data: " + attachmentData);
            
            // Validate required fields
            if (!attachmentData.containsKey("taskId") || !attachmentData.containsKey("uploadedById")) {
                return ResponseEntity.badRequest().body("Missing required fields: taskId or uploadedById");
            }
            
            Long taskId = Long.valueOf(attachmentData.get("taskId").toString());
            Long uploadedById = Long.valueOf(attachmentData.get("uploadedById").toString());
            
            System.out.println("Task ID: " + taskId + ", Uploaded by ID: " + uploadedById);
            
            Task task = taskRepository.findById(taskId).orElse(null);
            User uploadedBy = userRepository.findById(uploadedById).orElse(null);
            
            if (task == null) {
                System.err.println("Task not found with ID: " + taskId);
                return ResponseEntity.badRequest().body("Task not found with ID: " + taskId);
            }
            if (uploadedBy == null) {
                System.err.println("User not found with ID: " + uploadedById);
                return ResponseEntity.badRequest().body("User not found with ID: " + uploadedById);
            }
            
            // Validate attachment type
            String typeStr = (String) attachmentData.get("type");
            if (typeStr == null) {
                return ResponseEntity.badRequest().body("Attachment type is required");
            }
            
            TaskAttachment attachment = new TaskAttachment();
            attachment.setTask(task);
            attachment.setUploadedBy(uploadedBy);
            attachment.setFileName((String) attachmentData.get("fileName"));
            attachment.setType(TaskAttachment.AttachmentType.valueOf(typeStr));
            attachment.setFileUrl((String) attachmentData.get("fileUrl"));
            attachment.setDescription((String) attachmentData.get("description"));
            attachment.setApprovalStatus(TaskAttachment.ApprovalStatus.PENDING);
            attachment.setCreatedAt(LocalDateTime.now());
            
            System.out.println("Saving attachment: " + attachment.getFileName());
            TaskAttachment savedAttachment = taskAttachmentRepository.save(attachment);
            System.out.println("Attachment saved successfully with ID: " + savedAttachment.getId());
            
            return ResponseEntity.ok(savedAttachment);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid attachment type: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid attachment type: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating attachment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating attachment: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/review")
    public ResponseEntity<TaskAttachment> reviewAttachment(@PathVariable Long id, @RequestBody Map<String, Object> reviewData) {
        try {
            return taskAttachmentRepository.findById(id)
                    .map(attachment -> {
                        String status = (String) reviewData.get("approvalStatus");
                        Long reviewedById = Long.valueOf(reviewData.get("reviewedById").toString());
                        
                        User reviewedBy = userRepository.findById(reviewedById).orElse(null);
                        if (reviewedBy == null) {
                            return ResponseEntity.badRequest().<TaskAttachment>build();
                        }
                        
                        // Store previous status for logging
                        TaskAttachment.ApprovalStatus previousStatus = attachment.getApprovalStatus();
                        TaskAttachment.ApprovalStatus newStatus = TaskAttachment.ApprovalStatus.valueOf(status);
                        
                        attachment.setApprovalStatus(newStatus);
                        attachment.setReviewedBy(reviewedBy);
                        attachment.setReviewComment((String) reviewData.get("reviewComment"));
                        attachment.setReviewedAt(LocalDateTime.now());
                        
                        TaskAttachment savedAttachment = taskAttachmentRepository.save(attachment);
                        
                        // Create notification for attachment status change
                        notificationService.createAttachmentStatusNotification(
                            savedAttachment, reviewedBy, attachment.getUploadedBy(), 
                            status, (String) reviewData.get("reviewComment"));
                        
                        System.out.println("Attachment " + id + " status changed from " + previousStatus + " to " + newStatus + " by user " + reviewedById);
                        
                        return ResponseEntity.ok(savedAttachment);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error reviewing attachment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/revert")
    public ResponseEntity<TaskAttachment> revertAttachmentStatus(@PathVariable Long id, @RequestBody Map<String, Object> revertData) {
        try {
            return taskAttachmentRepository.findById(id)
                    .map(attachment -> {
                        Long reviewedById = Long.valueOf(revertData.get("reviewedById").toString());
                        String revertComment = (String) revertData.get("revertComment");
                        
                        User reviewedBy = userRepository.findById(reviewedById).orElse(null);
                        if (reviewedBy == null) {
                            return ResponseEntity.badRequest().<TaskAttachment>build();
                        }
                        
                        // Store previous status for logging
                        TaskAttachment.ApprovalStatus previousStatus = attachment.getApprovalStatus();
                        
                        // Revert to PENDING status
                        attachment.setApprovalStatus(TaskAttachment.ApprovalStatus.PENDING);
                        attachment.setReviewedBy(reviewedBy);
                        attachment.setReviewComment(revertComment);
                        attachment.setReviewedAt(LocalDateTime.now());
                        
                        TaskAttachment savedAttachment = taskAttachmentRepository.save(attachment);
                        
                        // Create notification for attachment status revert
                        notificationService.createAttachmentStatusNotification(
                            savedAttachment, reviewedBy, attachment.getUploadedBy(), 
                            "PENDING", revertComment);
                        
                        System.out.println("Attachment " + id + " status reverted from " + previousStatus + " to PENDING by user " + reviewedById);
                        
                        return ResponseEntity.ok(savedAttachment);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error reverting attachment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long id) {
        return taskAttachmentRepository.findById(id)
                .map(attachment -> {
                    taskAttachmentRepository.delete(attachment);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}