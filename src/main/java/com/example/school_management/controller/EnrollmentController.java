package com.example.school_management.controller;

import com.example.school_management.dto.EnrollmentRegistrationRequest;
import com.example.school_management.dto.EnrollmentScoreUpdateRequest;
import com.example.school_management.model.Enrollment;
import com.example.school_management.repository.EnrollmentRepository;
import com.example.school_management.service.EnrollmentService;
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
    private final EnrollmentService enrollmentService;

    @GetMapping
    public List<Enrollment> getAll() {
        return enrollmentRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Enrollment> create(@Valid @RequestBody Enrollment enrollment) {
        return new ResponseEntity<>(enrollmentRepository.save(enrollment), HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<Enrollment> register(@Valid @RequestBody EnrollmentRegistrationRequest request) {
        return new ResponseEntity<>(enrollmentService.register(request), HttpStatus.CREATED);
    }

    @PutMapping("/{enrollmentId}/scores")
    public ResponseEntity<Enrollment> updateScores(
            @PathVariable String enrollmentId,
            @Valid @RequestBody EnrollmentScoreUpdateRequest request
    ) {
        return ResponseEntity.ok(enrollmentService.updateScores(enrollmentId, request));
    }
}
