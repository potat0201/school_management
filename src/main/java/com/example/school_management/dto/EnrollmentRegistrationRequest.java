package com.example.school_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnrollmentRegistrationRequest {
    @NotBlank(message = "studentId không được để trống")
    private String studentId;

    @NotBlank(message = "classId không được để trống")
    private String classId;
}
