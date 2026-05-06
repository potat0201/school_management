package com.example.school_management.repository;

import com.example.school_management.model.Enrollment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {
    Optional<Enrollment> findByStudentIdAndClassId(String studentId, String classId);
    List<Enrollment> findByStudentId(String studentId);
    List<Enrollment> findByClassId(String classId);
}
