package com.in10nt.ems.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String admin123Hash = encoder.encode("admin123");
        String ceo123Hash = encoder.encode("ceo123");
        String emp123Hash = encoder.encode("emp123");
        
        System.out.println("=== BCrypt Hashes ===");
        System.out.println("admin123: " + admin123Hash);
        System.out.println("ceo123: " + ceo123Hash);
        System.out.println("emp123: " + emp123Hash);
        
        System.out.println("\n=== SQL Update Statements ===");
        System.out.println("UPDATE users SET password = '" + admin123Hash + "' WHERE email = 'admin@in10nt.com';");
        System.out.println("UPDATE users SET password = '" + ceo123Hash + "' WHERE email = 'ceo@in10nt.com';");
        System.out.println("UPDATE users SET password = '" + emp123Hash + "' WHERE email = 'employee@in10nt.com';");
    }
}