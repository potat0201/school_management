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
    public List<Attendance> getAll() {
        return attendanceRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Attendance> create(@Valid @RequestBody Attendance attendance) {
        return new ResponseEntity<>(attendanceRepository.save(attendance), HttpStatus.CREATED);
    }

    @PostMapping("/mark")
    public ResponseEntity<Attendance> mark(@Valid @RequestBody AttendanceMarkRequest request) {
        return ResponseEntity.ok(attendanceService.markAttendance(request));
    }
}
