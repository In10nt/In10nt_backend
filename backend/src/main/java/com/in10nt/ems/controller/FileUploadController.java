package com.in10nt.ems.controller;

import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {
    
    private final UserRepository userRepository;
    private static final String UPLOAD_DIR = "uploads/profiles/";
    
    @PostMapping("/profile-picture/{userId}")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // Update user profile picture path
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String profilePictureUrl = "/uploads/profiles/" + filename;
            user.setProfilePicture(profilePictureUrl);
            userRepository.save(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", profilePictureUrl);
            response.put("message", "Profile picture uploaded successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body("Failed to upload file: " + e.getMessage());
        }
    }
}
