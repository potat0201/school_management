package com.example.school_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data // Tự động sinh Getter/Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "students") // Ánh xạ với tên collection
public class Student {

    @Id
    private String id; // Trương ứng với _id trong MongoDB (chuỗi Hash)

    @Indexed(unique = true)
    @Field("student_code")
    private String studentCode;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String phone;

    @Field("gpa")
    @Builder.Default // Nếu bạn dùng pattern Builder, cần cái này cho giá trị mặc định
    private Double gpa = 0.0;

    @Field("total_credits")
    @Builder.Default
    private Integer totalCredits = 0;

    @Field("created_at")
    @Builder.Default
    private Date createdAt = new Date();
}
