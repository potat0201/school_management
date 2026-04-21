package com.example.school_management.service;

import com.example.school_management.dto.StudentDto;
import com.example.school_management.model.Student;
import com.example.school_management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok tự động tạo Constructor để inject Repository
public class StudentService {

    private final StudentRepository studentRepository;

    // 1. Hàm Thêm mới sinh viên
    public StudentDto createStudent(StudentDto dto) {
        // Kiểm tra xem email đã tồn tại chưa
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // Map từ DTO (Frontend gửi) sang Entity (Để lưu DB)
        Student student = Student.builder()
                .studentCode(dto.getStudentCode())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .gpa(0.0) // Gán cứng giá trị mặc định
                .totalCredits(0)
                .build();

        // Lưu vào MongoDB
        Student savedStudent = studentRepository.save(student);

        // Map ngược lại Entity ra DTO để trả về Frontend
        dto.setId(savedStudent.getId());
        return dto;
    }

    // 2. Hàm Tìm sinh viên theo Mã SV
    public StudentDto getStudentByCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên có mã: " + studentCode));

        // Tương tự, trả về DTO
        StudentDto responseDto = new StudentDto();
        responseDto.setId(student.getId());
        responseDto.setStudentCode(student.getStudentCode());
        responseDto.setName(student.getName());
        responseDto.setEmail(student.getEmail());
        responseDto.setPhone(student.getPhone());

        return responseDto;
    }
}