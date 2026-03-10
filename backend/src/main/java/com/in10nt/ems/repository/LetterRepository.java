package com.in10nt.ems.repository;

import com.in10nt.ems.model.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    List<Letter> findByEmployeeId(Long employeeId);
}
