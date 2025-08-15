package com.gk.repository;

import com.gk.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    List<Student> findByGrade(String grade);

    @Query("SELECT s FROM Student s WHERE s.attendance >= :minAttendance")
    List<Student> findByMinimumAttendance(@Param("minAttendance") double minAttendance);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByAttendanceRecordsId(Long recordId);

    @Query("SELECT s FROM Student s JOIN s.marks m WHERE m.subject = :subject AND m.marks >= :minMarks")
    List<Student> findBySubjectAndMinimumMarks(@Param("subject") String subject, @Param("minMarks") double minMarks);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.attendance < :threshold")
    long countStudentsWithLowAttendance(@Param("threshold") double threshold);

    @Query("SELECT s FROM Student s WHERE SIZE(s.feePayments) = 0 OR s.feePayments IS EMPTY")
    List<Student> findStudentsWithNoPendingFees();
}
