package com.in10nt.ems.controller;

import com.in10nt.ems.model.AttachmentComment;
import com.in10nt.ems.model.TaskAttachment;
import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.AttachmentCommentRepository;
import com.in10nt.ems.repository.TaskAttachmentRepository;
import com.in10nt.ems.repository.UserRepository;
import com.in10nt.ems.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attachment-comments")
@RequiredArgsConstructor
public class AttachmentCommentController {
    private final AttachmentCommentRepository attachmentCommentRepository;
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    @GetMapping("/attachment/{attachmentId}")
    public List<AttachmentComment> getCommentsByAttachmentId(@PathVariable Long attachmentId) {
        return attachmentCommentRepository.findByAttachmentIdOrderByCreatedAtAsc(attachmentId);
    }
    
    @GetMapping("/task/{taskId}")
    public List<AttachmentComment> getCommentsByTaskId(@PathVariable Long taskId) {
        return attachmentCommentRepository.findByAttachmentTaskIdOrderByCreatedAtDesc(taskId);
    }
    
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Map<String, Object> commentData) {
        try {
            Long attachmentId = Long.valueOf(commentData.get("attachmentId").toString());
            Long commentedById = Long.valueOf(commentData.get("commentedById").toString());
            String commentText = (String) commentData.get("commentText");
            String commentType = (String) commentData.getOrDefault("commentType", "COMMENT");
            
            TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId).orElse(null);
            User commentedBy = userRepository.findById(commentedById).orElse(null);
            
            if (attachment == null || commentedBy == null || commentText == null || commentText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid comment data");
            }
            
            AttachmentComment comment = new AttachmentComment();
            comment.setAttachment(attachment);
            comment.setCommentedBy(commentedBy);
            comment.setCommentText(commentText.trim());
            comment.setCommentType(AttachmentComment.CommentType.valueOf(commentType));
            comment.setCreatedAt(LocalDateTime.now());
            
            AttachmentComment savedComment = attachmentCommentRepository.save(comment);
            
            // Create notifications for relevant users
            if (comment.getCommentType() == AttachmentComment.CommentType.COMMENT) {
                // Notify the attachment uploader if they're not the commenter
                if (!attachment.getUploadedBy().getId().equals(commentedBy.getId())) {
                    notificationService.createAttachmentCommentNotification(
                        attachment, commentedBy, attachment.getUploadedBy(), commentText);
                }
                
                // Notify the task assignee if they're different from uploader and commenter
                if (attachment.getTask().getAssignedTo() != null && 
                    !attachment.getTask().getAssignedTo().getId().equals(commentedBy.getId()) &&
                    !attachment.getTask().getAssignedTo().getId().equals(attachment.getUploadedBy().getId())) {
                    notificationService.createAttachmentCommentNotification(
                        attachment, commentedBy, attachment.getTask().getAssignedTo(), commentText);
                }
                
                // Notify the task creator if they're different from all above
                if (attachment.getTask().getCreatedBy() != null && 
                    !attachment.getTask().getCreatedBy().getId().equals(commentedBy.getId()) &&
                    !attachment.getTask().getCreatedBy().getId().equals(attachment.getUploadedBy().getId()) &&
                    (attachment.getTask().getAssignedTo() == null || 
                     !attachment.getTask().getCreatedBy().getId().equals(attachment.getTask().getAssignedTo().getId()))) {
                    notificationService.createAttachmentCommentNotification(
                        attachment, commentedBy, attachment.getTask().getCreatedBy(), commentText);
                }
            }
            
            return ResponseEntity.ok(savedComment);
            
        } catch (Exception e) {
            System.err.println("Error creating comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating comment: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        return attachmentCommentRepository.findById(id)
                .map(comment -> {
                    attachmentCommentRepository.delete(comment);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}