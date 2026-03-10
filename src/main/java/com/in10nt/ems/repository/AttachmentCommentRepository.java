package com.in10nt.ems.repository;

import com.in10nt.ems.model.AttachmentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentCommentRepository extends JpaRepository<AttachmentComment, Long> {
    List<AttachmentComment> findByAttachmentIdOrderByCreatedAtAsc(Long attachmentId);
    List<AttachmentComment> findByAttachmentTaskIdOrderByCreatedAtDesc(Long taskId);
}