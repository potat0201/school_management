package com.example.school_management.controller;

import com.example.school_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;

    @GetMapping("/overview")
    public Map<String, Long> getOverview() {
        return Map.of(
                "students", studentRepository.count(),
                "teachers", teacherRepository.count(),
                "subjects", subjectRepository.count(),
                "semesters", semesterRepository.count(),
                "classes", classroomRepository.count(),
                "enrollments", enrollmentRepository.count(),
                "attendanceBuckets", attendanceRepository.count()
        );
    }
}
