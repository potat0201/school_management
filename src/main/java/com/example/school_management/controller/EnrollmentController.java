package com.example.school_management.controller;

import com.example.school_management.model.Enrollment;
import com.example.school_management.repository.EnrollmentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;

    @GetMapping
    public List<Enrollment> getAll() {
        return enrollmentRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Enrollment> create(@Valid @RequestBody Enrollment enrollment) {
        return new ResponseEntity<>(enrollmentRepository.save(enrollment), HttpStatus.CREATED);
    }
}
