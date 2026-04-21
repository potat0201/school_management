package com.example.school_management.controller;

import com.example.school_management.exception.NotFoundException;
import com.example.school_management.model.Semester;
import com.example.school_management.repository.SemesterRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final SemesterRepository semesterRepository;

    @GetMapping
    public List<Semester> getAll() {
        return semesterRepository.findAll();
    }

    @GetMapping("/{semesterCode}")
    public Semester getByCode(@PathVariable String semesterCode) {
        return semesterRepository.findBySemesterCode(semesterCode)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy học kỳ có mã: " + semesterCode));
    }

    @PostMapping
    public ResponseEntity<Semester> create(@Valid @RequestBody Semester semester) {
        return new ResponseEntity<>(semesterRepository.save(semester), HttpStatus.CREATED);
    }
}
