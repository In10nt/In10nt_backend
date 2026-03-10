package com.in10nt.ems.service;

import com.in10nt.ems.model.Notification;
import com.in10nt.ems.model.User;
import com.in10nt.ems.model.Task;
import com.in10nt.ems.model.TaskAttachment;
import com.in10nt.ems.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    
    public void createTaskAssignedNotification(Task task, User assignedTo, User assignedBy) {
        Notification notification = new Notification();
        notification.setUser(assignedTo);
        notification.setFromUser(assignedBy);
        notification.setTitle("New Task Assigned");
        notification.setMessage(String.format("You have been assigned a new task: %s", task.getTitle()));
        notification.setType(Notification.NotificationType.TASK_ASSIGNED);
        notification.setReferenceId(task.getId());
        notification.setReferenceType("TASK");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    public void createTaskCommentNotification(Task task, User commentedBy, User notifyUser, String commentText) {
        if (commentedBy.getId().equals(notifyUser.getId())) {
            return; // Don't notify yourself
        }
        
        Notification notification = new Notification();
        notification.setUser(notifyUser);
        notification.setFromUser(commentedBy);
        notification.setTitle("New Task Comment");
        notification.setMessage(String.format("%s commented on task '%s': %s", 
            commentedBy.getFullName(), task.getTitle(), 
            commentText.length() > 50 ? commentText.substring(0, 50) + "..." : commentText));
        notification.setType(Notification.NotificationType.TASK_COMMENT);
        notification.setReferenceId(task.getId());
        notification.setReferenceType("TASK");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    public void createTaskReminderNotification(Task task, User sentBy, User sentTo, String reminderText) {
        Notification notification = new Notification();
        notification.setUser(sentTo);
        notification.setFromUser(sentBy);
        notification.setTitle("Task Reminder");
        notification.setMessage(String.format("Reminder for task '%s': %s", 
            task.getTitle(), 
            reminderText.length() > 50 ? reminderText.substring(0, 50) + "..." : reminderText));
        notification.setType(Notification.NotificationType.TASK_REMINDER);
        notification.setReferenceId(task.getId());
        notification.setReferenceType("TASK");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    public void createAttachmentCommentNotification(TaskAttachment attachment, User commentedBy, User notifyUser, String commentText) {
        if (commentedBy.getId().equals(notifyUser.getId())) {
            return; // Don't notify yourself
        }
        
        Notification notification = new Notification();
        notification.setUser(notifyUser);
        notification.setFromUser(commentedBy);
        notification.setTitle("New Attachment Comment");
        notification.setMessage(String.format("%s commented on attachment '%s': %s", 
            commentedBy.getFullName(), attachment.getFileName(), 
            commentText.length() > 50 ? commentText.substring(0, 50) + "..." : commentText));
        notification.setType(Notification.NotificationType.ATTACHMENT_COMMENT);
        notification.setReferenceId(attachment.getId());
        notification.setReferenceType("ATTACHMENT");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    public void createAttachmentStatusNotification(TaskAttachment attachment, User reviewedBy, User notifyUser, String status, String comment) {
        if (reviewedBy.getId().equals(notifyUser.getId())) {
            return; // Don't notify yourself
        }
        
        Notification.NotificationType notificationType;
        String title;
        String message;
        
        switch (status.toUpperCase()) {
            case "APPROVED":
                notificationType = Notification.NotificationType.ATTACHMENT_APPROVED;
                title = "Attachment Approved";
                message = String.format("Your attachment '%s' has been approved by %s", 
                    attachment.getFileName(), reviewedBy.getFullName());
                break;
            case "REJECTED":
                notificationType = Notification.NotificationType.ATTACHMENT_REJECTED;
                title = "Attachment Rejected";
                message = String.format("Your attachment '%s' has been rejected by %s", 
                    attachment.getFileName(), reviewedBy.getFullName());
                break;
            case "PENDING":
                notificationType = Notification.NotificationType.ATTACHMENT_REVERTED;
                title = "Attachment Status Reverted";
                message = String.format("Your attachment '%s' status has been reverted to pending by %s", 
                    attachment.getFileName(), reviewedBy.getFullName());
                break;
            default:
                return; // Unknown status
        }
        
        if (comment != null && !comment.trim().isEmpty()) {
            message += String.format(". Comment: %s", 
                comment.length() > 50 ? comment.substring(0, 50) + "..." : comment);
        }
        
        Notification notification = new Notification();
        notification.setUser(notifyUser);
        notification.setFromUser(reviewedBy);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(notificationType);
        notification.setReferenceId(attachment.getId());
        notification.setReferenceType("ATTACHMENT");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    public void createTaskCompletedNotification(Task task, User completedBy, User notifyUser) {
        if (completedBy.getId().equals(notifyUser.getId())) {
            return; // Don't notify yourself
        }
        
        Notification notification = new Notification();
        notification.setUser(notifyUser);
        notification.setFromUser(completedBy);
        notification.setTitle("Task Completed");
        notification.setMessage(String.format("Task '%s' has been completed by %s", 
            task.getTitle(), completedBy.getFullName()));
        notification.setType(Notification.NotificationType.TASK_COMPLETED);
        notification.setReferenceId(task.getId());
        notification.setReferenceType("TASK");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
}