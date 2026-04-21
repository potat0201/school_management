package com.example.school_management.controller;

import com.example.school_management.dto.StudentDto;
import com.example.school_management.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // API: POST /api/students
    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        // @Valid sẽ kích hoạt việc kiểm tra @NotBlank, @Email bên trong DTO
        StudentDto createdStudent = studentService.createStudent(studentDto);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    // API: GET /api/students/{studentCode}
    @GetMapping("/{studentCode}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable String studentCode) {
        StudentDto student = studentService.getStudentByCode(studentCode);
        return ResponseEntity.ok(student);
    }
}