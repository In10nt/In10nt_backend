package com.in10nt.ems.repository;

import com.in10nt.ems.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedToId(Long userId);
    List<Task> findByCreatedById(Long userId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = ?1")
    Long countByStatus(Task.Status status);
    
    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN ?1 AND ?2")
    List<Task> findByDateRange(LocalDateTime start, LocalDateTime end);
}
