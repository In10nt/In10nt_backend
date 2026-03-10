package com.in10nt.ems.controller;

import com.in10nt.ems.model.Project;
import com.in10nt.ems.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectRepository projectRepository;
    
    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/manager/{managerId}")
    public List<Project> getProjectsByManager(@PathVariable Long managerId) {
        return projectRepository.findByManagerId(managerId);
    }
    
    @GetMapping("/member/{userId}")
    public List<Project> getProjectsByMember(@PathVariable Long userId) {
        return projectRepository.findByMemberId(userId);
    }
    
    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectRepository.save(project);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        return projectRepository.findById(id)
                .map(project -> {
                    project.setName(projectDetails.getName());
                    project.setDescription(projectDetails.getDescription());
                    project.setStartDate(projectDetails.getStartDate());
                    project.setEndDate(projectDetails.getEndDate());
                    project.setStatus(projectDetails.getStatus());
                    project.setManager(projectDetails.getManager());
                    project.setMembers(projectDetails.getMembers());
                    project.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(projectRepository.save(project));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(project -> {
                    projectRepository.delete(project);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}