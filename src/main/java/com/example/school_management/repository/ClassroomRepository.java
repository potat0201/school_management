package com.example.school_management.repository;

import com.example.school_management.model.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClassroomRepository extends MongoRepository<Classroom, String> {
    Optional<Classroom> findByClassCode(String classCode);
}
