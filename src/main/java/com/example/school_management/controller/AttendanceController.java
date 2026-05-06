package com.example.school_management.controller;

import com.example.school_management.dto.AttendanceMarkRequest;
import com.example.school_management.model.Attendance;
import com.example.school_management.repository.AttendanceRepository;
import com.example.school_management.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;

    @GetMapping
    public List<Attendance> getAll(
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String studentId
    ) {
        if (classId != null && studentId != null) {
            return attendanceRepository.findByStudentIdAndClassId(studentId, classId)
                    .map(List::of).orElse(List.of());
        }
        if (classId != null) return attendanceRepository.findByClassId(classId);
        if (studentId != null) return attendanceRepository.findByStudentId(studentId);
        return attendanceRepository.findAll();
    }

    @PostMapping("/mark")
    public ResponseEntity<Attendance> mark(@Valid @RequestBody AttendanceMarkRequest request) {
        return ResponseEntity.ok(attendanceService.markAttendance(request));
    }

    @PostMapping
    public ResponseEntity<Attendance> create(@Valid @RequestBody Attendance attendance) {
        return new ResponseEntity<>(attendanceRepository.save(attendance), HttpStatus.CREATED);
    }
}
