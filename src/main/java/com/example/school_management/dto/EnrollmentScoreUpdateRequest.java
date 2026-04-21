package com.example.school_management.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class EnrollmentScoreUpdateRequest {
    @DecimalMin(value = "0.0", message = "midterm phải >= 0")
    @DecimalMax(value = "10.0", message = "midterm phải <= 10")
    private Double midterm;

    @DecimalMin(value = "0.0", message = "final phải >= 0")
    @DecimalMax(value = "10.0", message = "final phải <= 10")
    private Double finall;

    @DecimalMin(value = "0.0", message = "assignment phải >= 0")
    @DecimalMax(value = "10.0", message = "assignment phải <= 10")
    private Double assignment;
}
