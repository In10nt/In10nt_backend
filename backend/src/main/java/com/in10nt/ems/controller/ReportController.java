package com.in10nt.ems.controller;

import com.in10nt.ems.repository.TaskRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final TaskRepository taskRepository;
    
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardStats(@RequestParam String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = switch (period) {
            case "daily" -> now.minusDays(1);
            case "weekly" -> now.minusWeeks(1);
            case "monthly" -> now.minusMonths(1);
            case "yearly" -> now.minusYears(1);
            default -> now.minusMonths(1);
        };
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTasks", taskRepository.count());
        stats.put("completedTasks", taskRepository.countByStatus(com.in10nt.ems.model.Task.Status.COMPLETED));
        stats.put("pendingTasks", taskRepository.countByStatus(com.in10nt.ems.model.Task.Status.PENDING));
        stats.put("inProgressTasks", taskRepository.countByStatus(com.in10nt.ems.model.Task.Status.IN_PROGRESS));
        stats.put("tasks", taskRepository.findByDateRange(start, now));
        
        return stats;
    }
}
