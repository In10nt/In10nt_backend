package com.in10nt.ems.dto;

import com.in10nt.ems.model.Task;
import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private Long assignedToId;
    private String dueDate;
    private Integer progress;
    private String comments;
}
