package com.example.school_management.repository;

import com.example.school_management.model.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TeacherRepository extends MongoRepository<Teacher, String> {
    Optional<Teacher> findByTeacherCode(String teacherCode);
}
