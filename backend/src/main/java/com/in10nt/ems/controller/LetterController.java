package com.in10nt.ems.controller;

import com.in10nt.ems.model.Letter;
import com.in10nt.ems.repository.LetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/letters")
@RequiredArgsConstructor
public class LetterController {
    private final LetterRepository letterRepository;
    
    @GetMapping
    public List<Letter> getAllLetters() {
        return letterRepository.findAll();
    }
    
    @GetMapping("/employee/{employeeId}")
    public List<Letter> getLettersByEmployee(@PathVariable Long employeeId) {
        return letterRepository.findByEmployeeId(employeeId);
    }
    
    @PostMapping
    public Letter sendLetter(@RequestBody Letter letter) {
        return letterRepository.save(letter);
    }
}
