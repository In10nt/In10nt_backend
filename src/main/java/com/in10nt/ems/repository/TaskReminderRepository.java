package com.in10nt.ems.repository;

import com.in10nt.ems.model.TaskReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskReminderRepository extends JpaRepository<TaskReminder, Long> {
    List<TaskReminder> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    List<TaskReminder> findBySentToIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    List<TaskReminder> findBySentToIdOrderByCreatedAtDesc(Long userId);
}