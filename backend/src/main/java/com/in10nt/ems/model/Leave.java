package com.in10nt.ems.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaves")
@Data
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @Enumerated(EnumType.STRING)
    private LeaveType type;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    
    private String rejectionReason;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum LeaveType {
        SICK, CASUAL, ANNUAL, UNPAID
    }
    
    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}
