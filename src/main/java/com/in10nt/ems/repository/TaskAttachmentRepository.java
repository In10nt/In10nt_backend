package com.in10nt.ems.repository;

import com.in10nt.ems.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
    List<TaskAttachment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    List<TaskAttachment> findByUploadedByIdOrderByCreatedAtDesc(Long userId);
    List<TaskAttachment> findByApprovalStatusOrderByCreatedAtDesc(TaskAttachment.ApprovalStatus status);
}