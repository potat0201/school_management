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

    @PutMapping("/{id}")
    public ResponseEntity<Semester> update(@PathVariable String id, @RequestBody Semester semester) {
        Semester existing = semesterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy học kỳ"));
        existing.setName(semester.getName());
        existing.setStartDate(semester.getStartDate());
        existing.setEndDate(semester.getEndDate());
        existing.setIsRegistrationOpen(semester.getIsRegistrationOpen());
        return ResponseEntity.ok(semesterRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!semesterRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy học kỳ");
        }
        semesterRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
