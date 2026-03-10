package com.in10nt.ems.controller;

import com.in10nt.ems.model.Subtask;
import com.in10nt.ems.model.Task;
import com.in10nt.ems.repository.SubtaskRepository;
import com.in10nt.ems.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subtasks")
@RequiredArgsConstructor
public class SubtaskController {
    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;
    
    @GetMapping("/task/{taskId}")
    public List<Subtask> getSubtasksByTaskId(@PathVariable Long taskId) {
        return subtaskRepository.findByParentTaskIdOrderByCreatedAtAsc(taskId);
    }
    
    @PostMapping
    public ResponseEntity<Subtask> createSubtask(@RequestBody Map<String, Object> subtaskData) {
        try {
            Long parentTaskId = Long.valueOf(subtaskData.get("parentTaskId").toString());
            Task parentTask = taskRepository.findById(parentTaskId).orElse(null);
            
            if (parentTask == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Subtask subtask = new Subtask();
            subtask.setTitle((String) subtaskData.get("title"));
            subtask.setDescription((String) subtaskData.get("description"));
            subtask.setParentTask(parentTask);
            subtask.setIsCompleted(false);
            subtask.setCreatedAt(LocalDateTime.now());
            subtask.setUpdatedAt(LocalDateTime.now());
            
            Subtask savedSubtask = subtaskRepository.save(subtask);
            return ResponseEntity.ok(savedSubtask);
        } catch (Exception e) {
            System.err.println("Error creating subtask: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Subtask> updateSubtask(@PathVariable Long id, @RequestBody Map<String, Object> subtaskData) {
        try {
            return subtaskRepository.findById(id)
                    .map(subtask -> {
                        if (subtaskData.get("title") != null) {
                            subtask.setTitle((String) subtaskData.get("title"));
                        }
                        if (subtaskData.get("description") != null) {
                            subtask.setDescription((String) subtaskData.get("description"));
                        }
                        if (subtaskData.get("isCompleted") != null) {
                            subtask.setIsCompleted((Boolean) subtaskData.get("isCompleted"));
                        }
                        subtask.setUpdatedAt(LocalDateTime.now());
                        
                        Subtask savedSubtask = subtaskRepository.save(subtask);
                        
                        // Update parent task progress
                        updateParentTaskProgress(subtask.getParentTask().getId());
                        
                        return ResponseEntity.ok(savedSubtask);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error updating subtask: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubtask(@PathVariable Long id) {
        return subtaskRepository.findById(id)
                .map(subtask -> {
                    Long parentTaskId = subtask.getParentTask().getId();
                    subtaskRepository.delete(subtask);
                    
                    // Update parent task progress
                    updateParentTaskProgress(parentTaskId);
                    
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    private void updateParentTaskProgress(Long taskId) {
        taskRepository.findById(taskId).ifPresent(task -> {
            List<Subtask> subtasks = subtaskRepository.findByParentTaskId(taskId);
            if (!subtasks.isEmpty()) {
                long completedCount = subtasks.stream()
                        .mapToLong(subtask -> subtask.getIsCompleted() ? 1 : 0)
                        .sum();
                int progress = (int) ((completedCount * 100) / subtasks.size());
                task.setProgress(progress);
                
                // Auto-complete task when all subtasks are done
                if (progress == 100 && task.getStatus() == Task.Status.IN_PROGRESS) {
                    task.setStatus(Task.Status.COMPLETED);
                }
                
                task.setUpdatedAt(LocalDateTime.now());
                taskRepository.save(task);
                
                System.out.println("Updated task " + taskId + " progress to " + progress + "%");
            }
        });
    }
}