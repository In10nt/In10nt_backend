package com.in10nt.ems.repository;

import com.in10nt.ems.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
    List<Subtask> findByParentTaskId(Long parentTaskId);
    List<Subtask> findByParentTaskIdOrderByCreatedAtAsc(Long parentTaskId);
}