package com.example.school_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.CompoundIndex;
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
@CompoundIndexes({
        @CompoundIndex(name = "teacher_idx", def = "{'teacher_id': 1}"),
        @CompoundIndex(name = "semester_idx", def = "{'semester_id': 1}"),
        @CompoundIndex(name = "schedule_room_idx", def = "{'schedules.room': 1}")
})
public class Classroom {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("class_code")
    @NotBlank(message = "classCode không được để trống")
    private String classCode;

    // Lưu trữ dưới dạng String thay vì ObjectId để dễ thao tác với JSON/Frontend hơn
    @Field("subject_id")
    @NotBlank(message = "subjectId không được để trống")
    private String subjectId;

    @Field("teacher_id")
    @NotBlank(message = "teacherId không được để trống")
    private String teacherId;

    @Field("semester_id")
    @NotBlank(message = "semesterId không được để trống")
    private String semesterId;

    // Mảng nhúng Lịch học
    @Valid
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
        @NotBlank(message = "dayOfWeek không được để trống")
        @Pattern(
                regexp = "Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday",
                message = "dayOfWeek phải thuộc Monday..Sunday"
        )
        private String dayOfWeek;

        @Field("time_slot")
        @NotBlank(message = "timeSlot không được để trống")
        private String timeSlot;

        @NotBlank(message = "room không được để trống")
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
