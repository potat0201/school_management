package com.example.school_management.controller;

import com.example.school_management.exception.NotFoundException;
import com.example.school_management.model.Subject;
import com.example.school_management.repository.SubjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;

    @GetMapping
    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    @GetMapping("/{subjectCode}")
    public Subject getByCode(@PathVariable String subjectCode) {
        return subjectRepository.findBySubjectCode(subjectCode)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy môn học có mã: " + subjectCode));
    }

    @PostMapping
    public ResponseEntity<Subject> create(@Valid @RequestBody Subject subject) {
        return new ResponseEntity<>(subjectRepository.save(subject), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> update(@PathVariable String id, @RequestBody Subject subject) {
        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy môn học"));
        existing.setName(subject.getName());
        existing.setCredits(subject.getCredits());
        existing.setDepartment(subject.getDepartment());
        return ResponseEntity.ok(subjectRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!subjectRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy môn học");
        }
        subjectRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
