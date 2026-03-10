package com.in10nt.ems.controller;

import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("UserController is working!");
    }
    
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public User createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            System.out.println("Updating user with ID: " + id);
            System.out.println("Request data: " + userDetails.toString());
            
            return userRepository.findById(id)
                    .map(user -> {
                        // Update basic fields
                        if (userDetails.getFullName() != null) {
                            user.setFullName(userDetails.getFullName());
                        }
                        if (userDetails.getEmail() != null) {
                            user.setEmail(userDetails.getEmail());
                        }
                        if (userDetails.getPhone() != null) {
                            user.setPhone(userDetails.getPhone());
                        }
                        if (userDetails.getAddress() != null) {
                            user.setAddress(userDetails.getAddress());
                        }
                        if (userDetails.getDepartment() != null) {
                            user.setDepartment(userDetails.getDepartment());
                        }
                        if (userDetails.getSalary() != null) {
                            user.setSalary(userDetails.getSalary());
                        }
                        if (userDetails.getRole() != null) {
                            user.setRole(userDetails.getRole());
                        }
                        
                        // Update profile picture if provided
                        if (userDetails.getProfilePicture() != null) {
                            System.out.println("Updating profile picture, length: " + userDetails.getProfilePicture().length());
                            user.setProfilePicture(userDetails.getProfilePicture());
                        }
                        
                        // Update password if provided
                        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                        }
                        
                        User savedUser = userRepository.save(user);
                        System.out.println("User updated successfully: " + savedUser.getId());
                        return ResponseEntity.ok(savedUser);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
