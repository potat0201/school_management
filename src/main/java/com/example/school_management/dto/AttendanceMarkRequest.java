package com.example.school_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class AttendanceMarkRequest {
    @NotBlank(message = "studentId không được để trống")
    private String studentId;

    @NotBlank(message = "classId không được để trống")
    private String classId;

    @NotBlank(message = "status không được để trống")
    private String status;

    private String note;

    private Date date;
}
