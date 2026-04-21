package com.example.school_management.service;

import com.example.school_management.dto.EnrollmentRegistrationRequest;
import com.example.school_management.dto.EnrollmentScoreUpdateRequest;
import com.example.school_management.exception.BadRequestException;
import com.example.school_management.exception.NotFoundException;
import com.example.school_management.model.Attendance;
import com.example.school_management.model.Classroom;
import com.example.school_management.model.Enrollment;
import com.example.school_management.model.Student;
import com.example.school_management.model.Subject;
import com.example.school_management.repository.AttendanceRepository;
import com.example.school_management.repository.ClassroomRepository;
import com.example.school_management.repository.EnrollmentRepository;
import com.example.school_management.repository.StudentRepository;
import com.example.school_management.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public Enrollment register(EnrollmentRegistrationRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sinh viên"));
        Classroom classroom = classroomRepository.findById(request.getClassId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lớp học"));

        enrollmentRepository.findByStudentIdAndClassId(student.getId(), classroom.getId())
                .ifPresent(e -> {
                    throw new BadRequestException("Sinh viên đã đăng ký lớp này");
                });

        Enrollment enrollment = Enrollment.builder()
                .studentId(student.getId())
                .classId(classroom.getId())
                .scores(new Enrollment.ScoreDetail())
                .finalScore(null)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        Attendance attendance = Attendance.builder()
                .studentId(student.getId())
                .classId(classroom.getId())
                .summary(Attendance.AttendanceSummary.builder().build())
                .records(new ArrayList<>())
                .build();
        attendanceRepository.save(attendance);

        List<Classroom.StudentSummary> students = classroom.getStudents() == null ? new ArrayList<>() : classroom.getStudents();
        boolean alreadyInClass = students.stream().anyMatch(s -> student.getId().equals(s.getStudentId()));
        if (!alreadyInClass) {
            students.add(new Classroom.StudentSummary(student.getId(), student.getStudentCode(), student.getName()));
            classroom.setStudents(students);
            classroomRepository.save(classroom);
        }

        return savedEnrollment;
    }

    public Enrollment updateScores(String enrollmentId, EnrollmentScoreUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy enrollment"));

        Enrollment.ScoreDetail scoreDetail = enrollment.getScores() == null ? new Enrollment.ScoreDetail() : enrollment.getScores();
        scoreDetail.setMidterm(request.getMidterm());
        scoreDetail.setFinall(request.getFinall());
        scoreDetail.setAssignment(request.getAssignment());
        enrollment.setScores(scoreDetail);
        enrollment.setFinalScore(calculateFinalScore(scoreDetail));

        Enrollment saved = enrollmentRepository.save(enrollment);
        recalculateStudentGpa(saved.getStudentId());
        return saved;
    }

    private Double calculateFinalScore(Enrollment.ScoreDetail scoreDetail) {
        if (scoreDetail == null) {
            return null;
        }
        if (scoreDetail.getMidterm() == null || scoreDetail.getFinall() == null || scoreDetail.getAssignment() == null) {
            return null;
        }
        return (scoreDetail.getMidterm() * 0.3) + (scoreDetail.getAssignment() * 0.2) + (scoreDetail.getFinall() * 0.5);
    }

    private void recalculateStudentGpa(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sinh viên"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        double weightedScoreSum = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getFinalScore() == null) {
                continue;
            }
            Classroom classroom = classroomRepository.findById(enrollment.getClassId()).orElse(null);
            if (classroom == null || classroom.getSubjectId() == null) {
                continue;
            }
            Subject subject = subjectRepository.findById(classroom.getSubjectId()).orElse(null);
            if (subject == null || subject.getCredits() == null || subject.getCredits() <= 0) {
                continue;
            }
            double gpa4Scale = map10To4(enrollment.getFinalScore());
            weightedScoreSum += gpa4Scale * subject.getCredits();
            totalCredits += subject.getCredits();
        }

        student.setTotalCredits(totalCredits);
        student.setGpa(totalCredits == 0 ? 0.0 : weightedScoreSum / totalCredits);
        studentRepository.save(student);
    }

    private double map10To4(double score10) {
        if (score10 >= 8.5) return 4.0;
        if (score10 >= 8.0) return 3.5;
        if (score10 >= 7.0) return 3.0;
        if (score10 >= 6.5) return 2.5;
        if (score10 >= 5.5) return 2.0;
        if (score10 >= 5.0) return 1.5;
        if (score10 >= 4.0) return 1.0;
        return 0.0;
    }
}
