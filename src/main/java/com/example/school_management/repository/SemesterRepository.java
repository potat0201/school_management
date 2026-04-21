package com.example.school_management.repository;

import com.example.school_management.model.Semester;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SemesterRepository extends MongoRepository<Semester, String> {
    Optional<Semester> findBySemesterCode(String semesterCode);
}
