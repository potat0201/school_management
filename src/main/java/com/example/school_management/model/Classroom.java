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
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "classes")
public class Classroom {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("class_code")
    private String classCode;

    // Lưu trữ dưới dạng String thay vì ObjectId để dễ thao tác với JSON/Frontend hơn
    @Field("subject_id")
    private String subjectId;

    @Field("teacher_id")
    private String teacherId;

    @Field("semester_id")
    private String semesterId;

    // Mảng nhúng Lịch học
    private List<Schedule> schedules;

    // Mảng nhúng danh sách sinh viên thu gọn (Subset Pattern)
    private List<StudentSummary> students;

    @Field("created_at")
    @Builder.Default
    private Date createdAt = new Date();

    // ==========================================
    // CÁC LỚP INNER CLASS CHO DỮ LIỆU NHÚNG
    // ==========================================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Schedule {
        @Field("day_of_week")
        private String dayOfWeek;

        @Field("time_slot")
        private String timeSlot;

        private String room;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentSummary {
        @Field("student_id")
        private String studentId;

        @Field("student_code")
        private String studentCode;

        private String name;
    }
}