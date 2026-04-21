package com.example.school_management.repository;

import com.example.school_management.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    // Spring Data sẽ "tự động" viết code cho 2 hàm này, bạn chỉ cần khai báo tên!
    Optional<Student> findByStudentCode(String studentCode);

    boolean existsByEmail(String email);
}
