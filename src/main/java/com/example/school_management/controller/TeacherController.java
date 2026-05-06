package com.example.school_management.controller;

import com.example.school_management.exception.NotFoundException;
import com.example.school_management.model.Teacher;
import com.example.school_management.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;

    @GetMapping
    public List<Teacher> getAll() {
        return teacherRepository.findAll();
    }

    @GetMapping("/{teacherCode}")
    public Teacher getByCode(@PathVariable String teacherCode) {
        return teacherRepository.findByTeacherCode(teacherCode)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giảng viên có mã: " + teacherCode));
    }

    @PostMapping
    public ResponseEntity<Teacher> create(@Valid @RequestBody Teacher teacher) {
        return new ResponseEntity<>(teacherRepository.save(teacher), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> update(@PathVariable String id, @RequestBody Teacher teacher) {
        Teacher existing = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giảng viên"));
        existing.setName(teacher.getName());
        existing.setDepartment(teacher.getDepartment());
        existing.setEmail(teacher.getEmail());
        return ResponseEntity.ok(teacherRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!teacherRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy giảng viên");
        }
        teacherRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
