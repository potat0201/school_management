package com.example.school_management.repository;

import com.example.school_management.model.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends MongoRepository<Classroom, String> {
    Optional<Classroom> findByClassCode(String classCode);

    List<Classroom> findByTeacherId(String teacherId);

    List<Classroom> findBySemesterId(String semesterId);

    List<Classroom> findByTeacherIdAndSemesterId(String teacherId, String semesterId);
}
