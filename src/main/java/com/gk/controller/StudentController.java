package com.gk.controller;

import com.gk.model.AttendanceRecord;
import com.gk.model.Student;
import com.gk.model.SubjectMark;
import com.gk.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/students")
@Validated
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/save")
    public String createStudent(@Valid Student student) {
        studentService.createStudent(student);
        return "redirect:/api/students"; // Redirect to the student list page after saving
    }

    @GetMapping("/new")
    public String showStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "students/form";
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        Student updatedStudent = studentService.updateStudent(id, studentDetails);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<Student> searchStudents(@RequestParam String query) {
        return studentService.searchStudents(query);
    }

    // Marks Management
    @PostMapping("/{id}/marks")
    public ResponseEntity<Student> addMark(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody SubjectMark mark) {
        studentService.addMarkToStudent(id, mark);
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/{id}/average-marks")
    public ResponseEntity<Double> getAverageMarks(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.calculateAverageMarks(id));
    }

    @GetMapping("/top-performers")
    public ResponseEntity<List<Student>> getTopPerformers(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(studentService.getTopPerformers(limit));
    }

    // Attendance Management
    @GetMapping("/attendance/today")
    public ResponseEntity<List<AttendanceRecord>> getTodayAttendance() {
        List<AttendanceRecord> records = studentService.getTodayAttendanceRecords();
        return ResponseEntity.ok(records);
    }

    @PostMapping("/attendance/mark")
    public ResponseEntity<Void> markAttendance(
            @RequestParam @NotNull Date date,
            @RequestParam @NotEmpty List<Long> studentIds,
            @RequestParam @NotEmpty List<String> statuses,
            @RequestParam(required = false) List<String> notes) {
        studentService.markAttendance(date, studentIds, statuses, notes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/fees")
    public ResponseEntity<Void> recordFeePayment(
            @PathVariable @NotNull Long id,
            @RequestParam @NotEmpty String feeType,
            @RequestParam @Min(0) double amount) {
        studentService.recordFeePayment(id, feeType, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/fees/receipt")
    public ResponseEntity<byte[]> getFeeReceipt(@RequestParam String transactionId) {
        byte[] receipt = studentService.generateFeeReceipt(transactionId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "receipt.pdf");
        return ResponseEntity.ok().headers(headers).body(receipt);
    }

    // Batch Operations
    @PutMapping("/batch/grade")
    public ResponseEntity<Void> updateGradeBatch(
            @RequestParam List<Long> studentIds,
            @RequestParam String grade) {
        studentService.updateGradeBatch(studentIds, grade);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/batch/attendance")
    public ResponseEntity<Void> recalculateAttendanceBatch(@RequestParam List<Long> studentIds) {
        studentService.recalculateAttendanceBatch(studentIds);
        return ResponseEntity.ok().build();
    }

    // Statistics and Reports
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentService.getTotalStudents());
        stats.put("averageAttendance", studentService.getAverageAttendance());
        stats.put("averageGrade", studentService.getAverageGrade());
        stats.put("totalCourses", studentService.getTotalCourses());
        stats.put("gradeDistribution", studentService.getGradeDistribution());
        stats.put("attendanceTrend", studentService.getAttendanceTrend());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] excelFile = studentService.exportToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        headers.setContentDispositionFormData("filename", "students.xlsx");
        return ResponseEntity.ok().headers(headers).body(excelFile);
    }

    // Parent Communication
    @PostMapping("/{id}/message")
    public ResponseEntity<Void> sendParentMessage(
            @PathVariable Long id,
            @RequestParam String subject,
            @RequestParam String message) {
        studentService.sendParentMessage(id, subject, message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/updates")
    public ResponseEntity<List<Map<String, Object>>> getRecentUpdates(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getRecentUpdates(id));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> getAcademicProgress(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getAcademicProgressData(id));
    }

    // Additional Reports
    @GetMapping("/{id}/attendance-report")
    public ResponseEntity<Map<String, Object>> getAttendanceReport(@PathVariable Long id) {
        Map<String, Object> report = new HashMap<>();
        report.put("totalClasses", studentService.getTotalClassesForStudent(id));
        report.put("attendedClasses", studentService.getAttendedClassesCount(id));
        report.put("missedClasses", studentService.getMissedClassesCount(id));
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}/academic-report")
    public ResponseEntity<Map<String, Object>> getAcademicReport(@PathVariable Long id) {
        Map<String, Object> report = new HashMap<>();
        report.put("averageMarks", studentService.calculateAverageMarks(id));
        report.put("teacherComments", studentService.getTeacherComments(id));
        report.put("areasForImprovement", studentService.getAreasForImprovement(id));
        return ResponseEntity.ok(report);
    }

    // Fee Reports
    @GetMapping("/fees/summary")
    public ResponseEntity<Map<String, Object>> getFeeSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRevenue", studentService.calculateTotalRevenue());
        summary.put("todayPayments", studentService.calculateTodayPayments());
        summary.put("transactionCount", studentService.getTodayTransactionCount());
        summary.put("pendingDues", studentService.calculateTotalPendingDues());
        summary.put("collectionRate", studentService.calculateCollectionRate());
        summary.put("revenueTrend", studentService.getRevenueTrend());
        summary.put("feeTypeDistribution", studentService.getFeeTypeDistribution());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/fees/recent-payments")
    public ResponseEntity<List<Map<String, Object>>> getRecentPayments() {
        return ResponseEntity.ok(studentService.getRecentPayments());
    }

    @GetMapping("/fees/due-payments")
    public ResponseEntity<List<Map<String, Object>>> getDuePayments() {
        return ResponseEntity.ok(studentService.getDuePayments());
    }

    @PostMapping("/{id}/fees/reminder")
    public ResponseEntity<Void> sendFeeReminder(@PathVariable Long id) {
        studentService.sendFeeReminder(id);
        return ResponseEntity.ok().build();
    }

    // Exception Handler
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
