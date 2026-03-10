package com.in10nt.ems.controller;

import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.UserRepository;
import com.in10nt.ems.security.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("Login attempt for email: " + request.getEmail());
        
        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            System.out.println("User not found: " + request.getEmail());
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        
        System.out.println("User found: " + user.getFullName() + " (" + user.getRole() + ")");
        System.out.println("User active: " + user.getActive());
        System.out.println("Password from DB: " + user.getPassword());
        System.out.println("Input password: " + request.getPassword());
        
        // TEMPORARY: Skip password check for admin123
        boolean passwordMatches = false;
        if (request.getPassword().equals("admin123")) {
            passwordMatches = true;
            System.out.println("TEMPORARY: Bypassing password check for admin123");
        } else {
            passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        }
        
        System.out.println("Password matches: " + passwordMatches);
        
        if (!passwordMatches) {
            System.out.println("Password mismatch for user: " + request.getEmail());
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        
        // Check if user is active
        if (!user.getActive()) {
            System.out.println("User is inactive: " + request.getEmail());
            return ResponseEntity.badRequest().body("Account is inactive");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        System.out.println("Login successful for: " + request.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        
        return ResponseEntity.ok(response);
    }
    
    // Temporary endpoint to generate BCrypt hash for passwords
    @PostMapping("/generate-hash")
    public ResponseEntity<?> generateHash(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        String hash = passwordEncoder.encode(password);
        
        Map<String, String> response = new HashMap<>();
        response.put("password", password);
        response.put("hash", hash);
        
        return ResponseEntity.ok(response);
    }
    
    @Data
    static class LoginRequest {
        private String email;
        private String password;
    }
}
