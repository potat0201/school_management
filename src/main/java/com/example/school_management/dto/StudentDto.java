package com.example.school_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentDto {
    private String id;

    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    @NotBlank(message = "Tên sinh viên không được để trống")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private String phone;

    private Double gpa;

    private Integer totalCredits;
}
