package com.in10nt.ems.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "letters")
@Data
public class Letter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "sent_by")
    private User sentBy;
    
    private LocalDateTime sentAt = LocalDateTime.now();
}
