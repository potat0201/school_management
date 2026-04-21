package com.example.school_management.service;

import com.example.school_management.dto.StudentDto;
import com.example.school_management.exception.BadRequestException;
import com.example.school_management.exception.NotFoundException;
import com.example.school_management.model.Student;
import com.example.school_management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentDto createStudent(StudentDto dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng!");
        }

        Student student = Student.builder()
                .studentCode(dto.getStudentCode())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .gpa(0.0)
                .totalCredits(0)
                .build();

        Student savedStudent = studentRepository.save(student);
        return mapToDto(savedStudent);
    }

    public StudentDto getStudentByCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sinh viên có mã: " + studentCode));
        return mapToDto(student);
    }

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream().map(this::mapToDto).toList();
    }

    private StudentDto mapToDto(Student student) {
        StudentDto responseDto = new StudentDto();
        responseDto.setId(student.getId());
        responseDto.setStudentCode(student.getStudentCode());
        responseDto.setName(student.getName());
        responseDto.setEmail(student.getEmail());
        responseDto.setPhone(student.getPhone());
        return responseDto;
    }
}
