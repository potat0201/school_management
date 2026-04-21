package com.example.school_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "attendance_buckets")
// 1 Sinh viên - 1 Lớp học = 1 Sổ điểm danh duy nhất
@CompoundIndex(name = "student_class_attendance_unique", def = "{'student_id': 1, 'class_id': 1}", unique = true)
public class Attendance {

    @Id
    private String id;

    @Field("student_id")
    private String studentId;

    @Field("class_id")
    private String classId;

    // Computed properties - Tổng hợp sẵn để show ra UI cho nhanh
    private AttendanceSummary summary;

    // Mảng chứa chi tiết từng ngày điểm danh
    private List<DailyRecord> records;

    // ==========================================
    // INNER CLASSES
    // ==========================================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendanceSummary {
        @Field("total_present")
        @Builder.Default
        private Integer totalPresent = 0;

        @Field("total_absent")
        @Builder.Default
        private Integer totalAbsent = 0;

        @Field("total_late")
        @Builder.Default
        private Integer totalLate = 0;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRecord {
        private Date date;

        // Trạng thái: "PRESENT", "ABSENT", "LATE"
        private String status;

        private String note;
    }
}