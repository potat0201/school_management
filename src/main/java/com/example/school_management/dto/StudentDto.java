package com.example.school_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentDto {
    private String id; // Trả về cho frontend khi tạo thành công

    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    @NotBlank(message = "Tên sinh viên không được để trống")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private String phone;

    // Lưu ý: Không có trường GPA hay TotalCredits ở đây vì lúc tạo mới mặc định là 0
}