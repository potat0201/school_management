package com.example.school_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "enrollments")
// ĐÁNH CHỈ MỤC KÉP: 1 Sinh viên chỉ được có 1 bản ghi trong 1 Lớp học
@CompoundIndex(name = "student_class_unique", def = "{'student_id': 1, 'class_id': 1}", unique = true)
public class Enrollment {

    @Id
    private String id;

    @Field("student_id")
    private String studentId;

    @Field("class_id")
    private String classId;

    private String semester; // Sao chép semester code vào đây để query báo cáo cho nhanh

    private ScoreDetail scores;

    @Field("final_score")
    private Double finalScore;

    // ==========================================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreDetail {
        private Double midterm;
        private Double finall; // Chữ 'final' là từ khóa Java, nên dùng 'finall' hoặc 'finalScore'
        private Double assignment;
    }
}