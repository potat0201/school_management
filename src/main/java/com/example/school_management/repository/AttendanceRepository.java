package com.example.school_management.repository;

import com.example.school_management.model.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {
    Optional<Attendance> findByStudentIdAndClassId(String studentId, String classId);
}
