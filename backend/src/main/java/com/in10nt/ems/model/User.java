package com.in10nt.ems.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(columnDefinition = "LONGTEXT")
    private String profilePicture;
    private String phone;
    private String address;
    private String department;
    private Double salary;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum Role {
        ADMIN, CEO, EMPLOYEE
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", phone='" + phone + '\'' +
                ", department='" + department + '\'' +
                ", profilePicture=" + (profilePicture != null ? "length=" + profilePicture.length() : "null") +
                '}';
    }
}
