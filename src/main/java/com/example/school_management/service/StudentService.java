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

        return mapToDto(studentRepository.save(student));
    }

    public StudentDto getStudentByCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sinh viên có mã: " + studentCode));
        return mapToDto(student);
    }

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream().map(this::mapToDto).toList();
    }

    public StudentDto updateStudent(String id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sinh viên"));

        if (!student.getEmail().equals(dto.getEmail()) && studentRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng!");
        }

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());

        return mapToDto(studentRepository.save(student));
    }

    public void deleteStudent(String id) {
        if (!studentRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy sinh viên");
        }
        studentRepository.deleteById(id);
    }

    private StudentDto mapToDto(Student student) {
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setStudentCode(student.getStudentCode());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setGpa(student.getGpa());
        dto.setTotalCredits(student.getTotalCredits());
        return dto;
    }
}
