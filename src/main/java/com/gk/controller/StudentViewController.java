package com.gk.controller;

import com.gk.model.Student;
import com.gk.model.SubjectMark;
import com.gk.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/students")
public class StudentViewController {
    private final StudentService studentService;

    public StudentViewController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Basic Student Management
    @GetMapping
    public String redirectToList() {
        return "redirect:/students/list";
    }

    @GetMapping("/list")
    public String listStudents(Model model, @RequestParam(required = false) String search) {
        List<Student> students;
        if (search != null && !search.isEmpty()) {
            students = studentService.searchStudents(search);
        } else {
            students = studentService.getAllStudents();
        }
        model.addAttribute("students", students);
        return "students/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());
        return "students/form";
    }

    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute Student student,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "students/form";
        }

        try {
            studentService.createStudent(student);
            redirectAttributes.addFlashAttribute("message", "Student saved successfully!");
            return "redirect:/students/list";
        } catch (Exception e) {
            result.rejectValue("email", "error.student", e.getMessage());
            return "students/form";
        }
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        try {
            Student student = studentService.getStudentById(id);
            if (student == null) {
                throw new RuntimeException("Student not found with id: " + id);
            }
            model.addAttribute("student", student);
            if (student.getMarks() != null) {
                model.addAttribute("averageMarks", studentService.calculateAverageMarks(id));
            }
            model.addAttribute("attendance", student.getAttendance());
            return "students/details";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading student details: " + e.getMessage());
            return "redirect:/students/list";
        }
    }

    @GetMapping("/{id}/edit")
    public String editStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "students/form";
    }

    @PostMapping("/{id}/update")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute Student student,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "students/form";
        }

        try {
            studentService.updateStudent(id, student);
            redirectAttributes.addFlashAttribute("message", "Student updated successfully!");
            return "redirect:/students/" + id;
        } catch (Exception e) {
            result.rejectValue("email", "error.student", e.getMessage());
            return "students/form";
        }
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("totalStudents", studentService.getTotalStudents());
        model.addAttribute("averageAttendance", studentService.getAverageAttendance());
        model.addAttribute("averageGrade", studentService.getAverageGrade());
        model.addAttribute("gradeDistribution", studentService.getGradeDistribution());
        model.addAttribute("attendanceTrend", studentService.getAttendanceTrend());
        model.addAttribute("topPerformers", studentService.getTopPerformers(5));
        return "students/dashboard";
    }

    @GetMapping("/view/marks")
    public String viewMarkPage(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("marks", new SubjectMark());
        return "students/marks";
    }

    // Marks Management
    @GetMapping("/marks/{id}")
    public String showMarksPage(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("newMark", new SubjectMark());
        return "students/marks";
    }

    @PostMapping("/{id}/marks")
    public String addMark(@PathVariable Long id, @ModelAttribute SubjectMark mark,
                          RedirectAttributes redirectAttributes) {
        studentService.addMarkToStudent(id, mark);
        redirectAttributes.addFlashAttribute("message", "Mark added successfully!");
        return "redirect:/students/marks/" + id;
    }

    // Attendance Management
    @GetMapping("/attendance")
    public String showAttendancePage(Model model) {
        model.addAttribute("todayRecords", studentService.getTodayAttendanceRecords());
        model.addAttribute("students", studentService.getAllStudents());
        return "students/attendance";
    }

    @PostMapping("/attendance/mark")
    public String markAttendance(@RequestParam Date date,
                                 @RequestParam List<Long> studentIds,
                                 @RequestParam List<String> statuses,
                                 @RequestParam(required = false) List<String> notes,
                                 RedirectAttributes redirectAttributes) {
        studentService.markAttendance(date, studentIds, statuses, notes);
        redirectAttributes.addFlashAttribute("message", "Attendance marked successfully!");
        return "redirect:/students/attendance";
    }

    // Fee Management
    @GetMapping("/fees")
    public String showFeesPage(@RequestParam(required = false) Long studentId, Model model) {
        if (studentId != null) {
            Student student = studentService.getStudentById(studentId);
            model.addAttribute("student", student);
            model.addAttribute("dueAmount", studentService.calculateDueAmount(studentId));
            model.addAttribute("paidAmount", studentService.calculatePaidAmount(studentId));
        }
        model.addAttribute("students", studentService.getAllStudents());
        return "students/fees";
    }

    @GetMapping("/fees/summary")
    public String showFeesSummary(Model model) {
        model.addAttribute("totalRevenue", studentService.calculateTotalRevenue());
        model.addAttribute("todayPayments", studentService.calculateTodayPayments());
        model.addAttribute("pendingDues", studentService.calculateTotalPendingDues());
        model.addAttribute("collectionRate", studentService.calculateCollectionRate());
        model.addAttribute("recentPayments", studentService.getRecentPayments());
        model.addAttribute("duePayments", studentService.getDuePayments());
        model.addAttribute("revenueTrend", studentService.getRevenueTrend());
        return "students/fees-summary";
    }

    @PostMapping("/{id}/fees/pay")
    public String recordPayment(@PathVariable Long id,
                                @RequestParam String feeType,
                                @RequestParam double amount,
                                RedirectAttributes redirectAttributes) {
        studentService.recordFeePayment(id, feeType, amount);
        redirectAttributes.addFlashAttribute("message", "Payment recorded successfully!");
        return "redirect:/students/fees?studentId=" + id;
    }

    // Reports
    @GetMapping("/{id}/report")
    public String showStudentReport(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("academicProgress", studentService.getAcademicProgressData(id));
        model.addAttribute("attendanceReport", Map.of(
                "totalClasses", studentService.getTotalClassesForStudent(id),
                "attendedClasses", studentService.getAttendedClassesCount(id),
                "missedClasses", studentService.getMissedClassesCount(id)
        ));
        model.addAttribute("teacherComments", studentService.getTeacherComments(id));
        model.addAttribute("improvements", studentService.getAreasForImprovement(id));
        return "students/report";
    }

    // Batch Operations
    @GetMapping("/batch")
    public String showBatchOperationsPage(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students/batch";
    }

    @PostMapping("/batch/grade")
    public String updateGradeBatch(@RequestParam List<Long> studentIds,
                                   @RequestParam String grade,
                                   RedirectAttributes redirectAttributes) {
        studentService.updateGradeBatch(studentIds, grade);
        redirectAttributes.addFlashAttribute("message", "Grades updated successfully!");
        return "redirect:/students/batch";
    }

    // Parent Dashboard
    @GetMapping("/parent-dashboard/{id}")
    public String showParentDashboard(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("recentUpdates", studentService.getRecentUpdates(id));
        model.addAttribute("academicProgress", studentService.getAcademicProgressData(id));
        model.addAttribute("dueAmount", studentService.calculateDueAmount(id));
        model.addAttribute("nextDueDate", studentService.getNextDueDate(id));
        return "students/parent-dashboard";
    }

    // Export Data
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStudentData() {
        byte[] data = studentService.exportToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        headers.setContentDispositionFormData("filename", "students.xlsx");
        return ResponseEntity.ok().headers(headers).body(data);
    }
}
