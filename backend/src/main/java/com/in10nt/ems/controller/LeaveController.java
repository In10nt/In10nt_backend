package com.in10nt.ems.controller;

import com.in10nt.ems.model.Leave;
import com.in10nt.ems.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveRepository leaveRepository;
    
    @GetMapping
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }
    
    @GetMapping("/employee/{employeeId}")
    public List<Leave> getLeavesByEmployee(@PathVariable Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }
    
    @PostMapping
    public Leave createLeave(@RequestBody Leave leave) {
        return leaveRepository.save(leave);
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<Leave> approveLeave(@PathVariable Long id) {
        return leaveRepository.findById(id)
                .map(leave -> {
                    leave.setStatus(Leave.Status.APPROVED);
                    return ResponseEntity.ok(leaveRepository.save(leave));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<Leave> rejectLeave(@PathVariable Long id, @RequestBody String reason) {
        return leaveRepository.findById(id)
                .map(leave -> {
                    leave.setStatus(Leave.Status.REJECTED);
                    leave.setRejectionReason(reason);
                    return ResponseEntity.ok(leaveRepository.save(leave));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
