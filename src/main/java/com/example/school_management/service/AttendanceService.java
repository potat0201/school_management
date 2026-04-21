package com.example.school_management.service;

import com.example.school_management.dto.AttendanceMarkRequest;
import com.example.school_management.exception.BadRequestException;
import com.example.school_management.exception.NotFoundException;
import com.example.school_management.model.Attendance;
import com.example.school_management.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public Attendance markAttendance(AttendanceMarkRequest request) {
        Attendance attendance = attendanceRepository.findByStudentIdAndClassId(request.getStudentId(), request.getClassId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy attendance bucket của sinh viên trong lớp"));

        String normalizedStatus = request.getStatus().trim().toUpperCase(Locale.ROOT);
        if (!normalizedStatus.equals("PRESENT") && !normalizedStatus.equals("ABSENT") && !normalizedStatus.equals("LATE")) {
            throw new BadRequestException("status chỉ chấp nhận PRESENT, ABSENT, LATE");
        }

        if (attendance.getRecords() == null) {
            attendance.setRecords(new ArrayList<>());
        }
        if (attendance.getSummary() == null) {
            attendance.setSummary(Attendance.AttendanceSummary.builder().build());
        }

        Attendance.DailyRecord record = new Attendance.DailyRecord();
        record.setDate(request.getDate() == null ? new Date() : request.getDate());
        record.setStatus(normalizedStatus);
        record.setNote(request.getNote());
        attendance.getRecords().add(record);

        Attendance.AttendanceSummary summary = attendance.getSummary();
        if (summary.getTotalPresent() == null) summary.setTotalPresent(0);
        if (summary.getTotalAbsent() == null) summary.setTotalAbsent(0);
        if (summary.getTotalLate() == null) summary.setTotalLate(0);

        switch (normalizedStatus) {
            case "PRESENT" -> summary.setTotalPresent(summary.getTotalPresent() + 1);
            case "ABSENT" -> summary.setTotalAbsent(summary.getTotalAbsent() + 1);
            case "LATE" -> summary.setTotalLate(summary.getTotalLate() + 1);
        }

        attendance.setSummary(summary);
        return attendanceRepository.save(attendance);
    }
}
