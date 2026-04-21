package com.example.school_management.controller;

import com.example.school_management.exception.NotFoundException;
import com.example.school_management.model.Classroom;
import com.example.school_management.repository.ClassroomRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomRepository classroomRepository;

    @GetMapping
    public List<Classroom> getAll(
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String semesterId
    ) {
        if (teacherId != null && semesterId != null) {
            return classroomRepository.findByTeacherIdAndSemesterId(teacherId, semesterId);
        }
        if (teacherId != null) {
            return classroomRepository.findByTeacherId(teacherId);
        }
        if (semesterId != null) {
            return classroomRepository.findBySemesterId(semesterId);
        }
        return classroomRepository.findAll();
    }

    @GetMapping("/{classCode}")
    public Classroom getByCode(@PathVariable String classCode) {
        return classroomRepository.findByClassCode(classCode)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lớp học có mã: " + classCode));
    }

    @PostMapping
    public ResponseEntity<Classroom> create(@Valid @RequestBody Classroom classroom) {
        return new ResponseEntity<>(classroomRepository.save(classroom), HttpStatus.CREATED);
    }
}
