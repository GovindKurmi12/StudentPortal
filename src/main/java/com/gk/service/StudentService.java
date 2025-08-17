package com.gk.service;

import com.gk.dto.AttendanceStatus;
import com.gk.dto.FeeDetail;
import com.gk.model.*;
import com.gk.repository.AttendanceRepository;
import com.gk.repository.StudentRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    // Basic Student Operations
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public Student updateStudent(Long id, Student studentDetails) {
        Student student = getStudentById(id);
        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setPhoneNumber(studentDetails.getPhoneNumber());
        student.setGrade(studentDetails.getGrade());
        student.setAddress(studentDetails.getAddress());
        student.setDateOfBirth(studentDetails.getDateOfBirth());
        student.setParentEmail(studentDetails.getParentEmail());
        student.setParentPhone(studentDetails.getParentPhone());
        student.setBloodGroup(studentDetails.getBloodGroup());
        student.setEmergencyContact(studentDetails.getEmergencyContact());
        student.setAdmissionDate(studentDetails.getAdmissionDate());
        student.setSection(studentDetails.getSection());
        student.setParentName(studentDetails.getParentName());
        student.setParentOccupation(studentDetails.getParentOccupation());
        student.setAnnualIncome(studentDetails.getAnnualIncome());

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> searchStudents(String query) {
        return studentRepository.findAll().stream()
                .filter(student ->
                        student.getName().toLowerCase().contains(query.toLowerCase()) ||
                                student.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                                student.getGrade().toLowerCase().contains(query.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    // Marks Management
    public void addMarkToStudent(Long studentId, SubjectMark mark) {
        Student student = getStudentById(studentId);
        student.getMarks().add(mark);
        studentRepository.save(student);
    }

    public double calculateAverageMarks(Long studentId) {
        Student student = getStudentById(studentId);
        if (student.getMarks().isEmpty()) {
            return 0.0;
        }
        return student.getMarks().stream()
                .mapToDouble(SubjectMark::getMarks)
                .average()
                .orElse(0.0);
    }

    public List<Student> getTopPerformers(int limit) {
        return studentRepository.findAll().stream()
                .peek(student -> student.setAverageScore(calculateAverageMarks(student.getId())))
                .sorted((s1, s2) -> Double.compare(
                        s2.getAverageScore(),
                        s1.getAverageScore()
                ))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Attendance Management
    public List<AttendanceRecord> getTodayAttendanceRecords() {
        Date today = new Date();
        return getAllStudents().stream()
                .map(student -> {
                    AttendanceRecord record = new AttendanceRecord();
                    record.setStudent(student);
                    record.setDate(today);
                    record.setStatus(AttendanceStatus.ABSENT);
                    return record;
                })
                .collect(Collectors.toList());
    }

    public void markAttendance(Date date, List<Long> studentIds,
                               List<String> statuses, List<String> notes) {
        for (int i = 0; i < studentIds.size(); i++) {
            Student student = getStudentById(studentIds.get(i));
            AttendanceRecord record = new AttendanceRecord();
            record.setStudent(student);
            record.setDate(date);
            record.setStatus(AttendanceStatus.valueOf(statuses.get(i)));
            if (notes != null && i < notes.size()) {
                record.setNotes(notes.get(i));
            }
            record.setMarkedBy("System");
            student.getAttendanceRecords().add(record);
            studentRepository.save(student);
        }
    }

    // Fee Management
    public void recordFeePayment(Long studentId, String feeType, double amount) {
        Student student = getStudentById(studentId);
        FeeDetail payment = new FeeDetail();
        payment.setFeeType(feeType);
        payment.setAmount(amount);
        payment.setPaidDate(new Date());
        payment.setStatus("PAID");
        payment.setTransactionId(generateTransactionId());
        student.getFeePayments().add(payment);
        studentRepository.save(student);
    }

    public double calculateTotalFees(Long studentId) {
        return getStudentById(studentId).getCourses().size() * 500.0;
    }

    public double calculatePaidAmount(Long studentId) {
        Student student = getStudentById(studentId);
        return student.getFeePayments().stream()
                .filter(payment -> "PAID".equals(payment.getStatus()))
                .mapToDouble(FeeDetail::getAmount)
                .sum();
    }

    public double calculateDueAmount(Long studentId) {
        return calculateTotalFees(studentId) - calculatePaidAmount(studentId);
    }

    // Event Management
    public void addEventToStudent(Long studentId, StudentEvent event) {
        Student student = getStudentById(studentId);
        event.setStudent(student);
        student.getEvents().add(event);
        studentRepository.save(student);
    }

    public void removeEventFromStudent(Long studentId, Long eventId) {
        Student student = getStudentById(studentId);
        student.getEvents().removeIf(event -> event.getId().equals(eventId));
        studentRepository.save(student);
    }

    public List<StudentEvent> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return getAllStudents().stream()
                .flatMap(student -> student.getEvents().stream())
                .filter(event -> event.getStart().isAfter(now))
                .sorted(Comparator.comparing(StudentEvent::getStart))
                .collect(Collectors.toList());
    }

    // Parent Communication
    public void sendParentMessage(Long studentId, String subject, String message) {
        Student student = getStudentById(studentId);
        if (student.getParentEmail() != null) {
            // TODO: Implement email sending using JavaMailSender
            logger.info("Email sent to {} - Subject: {}", student.getParentEmail(), subject);
            logger.info("Message: {}", message);
        } else {
            logger.warn("Cannot send email - no parent email for student: {}", student.getName());
        }
    }

    // Fee Management Methods
    public Map<String, Object> getFeeSummary(Long studentId) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalFees", calculateTotalFees(studentId));
        summary.put("paidAmount", calculatePaidAmount(studentId));
        summary.put("dueAmount", calculateDueAmount(studentId));
        summary.put("nextDueDate", LocalDate.now().plusMonths(1));
        summary.put("paymentHistory", getPaymentHistory(studentId));
        return summary;
    }

    // Statistics and Reports
    @SuppressWarnings("unused") // Method is used by the view layer
    public Map<String, Object> generateStudentReport(Long studentId) {
        Student student = getStudentById(studentId);
        Map<String, Object> report = new HashMap<>();
        report.put("studentInfo", student);
        report.put("averageMarks", calculateAverageMarks(studentId));
        report.put("attendance", student.getAttendance());
        report.put("totalFees", calculateTotalFees(studentId));
        report.put("paidFees", calculatePaidAmount(studentId));
        report.put("dueFees", calculateDueAmount(studentId));
        report.put("upcomingEvents", student.getEvents().stream()
                .filter(event -> event.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList()));
        return report;
    }

    @SuppressWarnings("unused") // Method is used by the view layer
    public List<Map<String, Object>> getRecentUpdates(Long studentId) {
        Student student = getStudentById(studentId);
        List<Map<String, Object>> updates = new ArrayList<>();

        // Add recent marks
        student.getMarks().stream()
                .sorted(Comparator.comparing(SubjectMark::getDate).reversed())
                .limit(5)
                .forEach(mark -> {
                    Map<String, Object> update = new HashMap<>();
                    update.put("type", "MARK");
                    update.put("subject", mark.getSubject());
                    update.put("value", mark.getMarks());
                    update.put("date", mark.getDate());
                    updates.add(update);
                });

        // Add recent attendance
        student.getAttendanceRecords().stream()
                .sorted(Comparator.comparing(AttendanceRecord::getDate).reversed())
                .limit(5)
                .forEach(record -> {
                    Map<String, Object> update = new HashMap<>();
                    update.put("type", "ATTENDANCE");
                    update.put("status", record.getStatus());
                    update.put("date", record.getDate());
                    update.put("notes", record.getNotes());
                    updates.add(update);
                });

        // Add recent events
        student.getEvents().stream()
                .sorted(Comparator.comparing(StudentEvent::getStart).reversed())
                .limit(5)
                .forEach(event -> {
                    Map<String, Object> update = new HashMap<>();
                    update.put("type", "EVENT");
                    update.put("title", event.getTitle());
                    update.put("date", event.getStart());
                    update.put("description", event.getDescription());
                    updates.add(update);
                });

        return sortUpdates(updates);
    }

    @SuppressWarnings("unused")
    private List<Map<String, Object>> sortUpdates(List<Map<String, Object>> updates) {
        return updates.stream()
                .sorted((a, b) -> {
                    Object date1 = a.get("date");
                    Object date2 = b.get("date");

                    Date d1 = convertToDate(date1);
                    Date d2 = convertToDate(date2);

                    return d2.compareTo(d1);
                })
                .collect(Collectors.toList());
    }

    private Date convertToDate(Object dateObj) {
        if (dateObj instanceof Date) {
            return (Date) dateObj;
        } else if (dateObj instanceof LocalDateTime) {
            LocalDateTime ldt = (LocalDateTime) dateObj;
            return Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
        }
        throw new IllegalArgumentException("Unsupported date type: " + dateObj.getClass());
    }

    private Object convertToDateOrLocalDateTime(Object dateObj) {
        if (dateObj instanceof Date) {
            return dateObj;
        } else if (dateObj instanceof LocalDateTime dateTime) {
            return Date.from(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
        }
        throw new IllegalArgumentException("Unsupported date type: " + dateObj.getClass());
    }

    private Date now() {
        return new Date();
    }

    // Export functionality
    public byte[] exportToExcel() {
        logger.info("Starting Excel export process");
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Students");
            logger.debug("Created Excel sheet");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Email", "Phone", "Grade", "Section", "Parent Name", "Parent Email",
                    "Parent Phone", "Address", "Blood Group", "Admission Date", "Attendance %"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }
            logger.debug("Added header row");

            // Create data rows
            List<Student> students = getAllStudents();
            logger.info("Retrieved {} students for export", students.size());
            int rowNum = 1;

            try {
                for (Student student : students) {
                    if (student != null) {
                        logger.debug("Processing student: {}", student.getId());
                        Row row = sheet.createRow(rowNum++);

                        try {
                            row.createCell(1).setCellValue(student.getName() != null ? student.getName() : "");
                            row.createCell(2).setCellValue(student.getEmail() != null ? student.getEmail() : "");
                            row.createCell(3).setCellValue(student.getPhoneNumber() != null ? student.getPhoneNumber() : "");
                            row.createCell(4).setCellValue(student.getGrade() != null ? student.getGrade() : "");
                            row.createCell(5).setCellValue(student.getSection() != null ? student.getSection() : "");
                            row.createCell(6).setCellValue(student.getParentName() != null ? student.getParentName() : "");
                            row.createCell(7).setCellValue(student.getParentEmail() != null ? student.getParentEmail() : "");
                            row.createCell(8).setCellValue(student.getParentPhone() != null ? student.getParentPhone() : "");
                            row.createCell(9).setCellValue(student.getAddress() != null ? student.getAddress() : "");
                            row.createCell(10).setCellValue(student.getBloodGroup() != null ? student.getBloodGroup() : "");
                            row.createCell(11).setCellValue(student.getAdmissionDate() != null ?
                                    student.getAdmissionDate().toString() : "");
                            row.createCell(12).setCellValue(String.format("%.2f", student.getAttendance()));
                        } catch (Exception e) {
                            logger.error("Error processing student data: {} - {}", student.getId(), e.getMessage());
                        }
                    }
                }
                logger.debug("Completed processing all students");
            } catch (Exception e) {
                logger.error("Error during student data processing", e);
                throw new RuntimeException("Error processing student data: " + e.getMessage());
            }

            // Autosize columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            logger.debug("Adjusted column widths");

            workbook.write(outputStream);
            logger.info("Successfully completed Excel export");
            return outputStream.toByteArray();

        } catch (IOException e) {
            logger.error("Failed to create Excel file", e);
            throw new RuntimeException("Failed to create Excel file: " + e.getMessage());
        }
    }

    // Helper Methods
    public Date getNextDueDate(Long studentId) {
        // TODO: Implement based on fee schedule
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    private List<Map<String, Object>> getPaymentHistory(Long studentId) {
        Student student = getStudentById(studentId);
        return student.getFeePayments().stream()
                .map(payment -> {
                    Map<String, Object> history = new HashMap<>();
                    history.put("date", payment.getPaidDate());
                    history.put("amount", payment.getAmount());
                    history.put("type", payment.getFeeType());
                    history.put("status", payment.getStatus());
                    history.put("transactionId", payment.getTransactionId());
                    return history;
                })
                .collect(Collectors.toList());
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    // Batch Operations
    public void updateGradeBatch(List<Long> studentIds, String grade) {
        studentIds.forEach(id -> {
            Student student = getStudentById(id);
            student.setGrade(grade);
            studentRepository.save(student);
        });
    }

    public void recalculateAttendanceBatch(List<Long> studentIds) {
        studentIds.forEach(id -> {
            Student student = getStudentById(id);
            double attendancePercentage = calculateAttendancePercentage(student);
            student.setAttendancePercentage(attendancePercentage);
            studentRepository.save(student);
        });
    }

    private double calculateAttendancePercentage(Student student) {
        if (student.getAttendanceRecords().isEmpty()) {
            return 0.0;
        }
        long presentCount = student.getAttendanceRecords().stream()
                .filter(record -> record.getStatus() == AttendanceStatus.PRESENT)
                .count();
        return (double) presentCount / student.getAttendanceRecords().size() * 100;
    }

    // Fee Statistics and Reports
    public double calculateTotalRevenue() {
        return studentRepository.findAll().stream()
                .flatMap(student -> student.getFeePayments().stream())
                .filter(payment -> "PAID".equals(payment.getStatus()))
                .mapToDouble(FeeDetail::getAmount)
                .sum();
    }

    public double calculateTodayPayments() {
        Date today = new Date();
        return studentRepository.findAll().stream()
                .flatMap(student -> student.getFeePayments().stream())
                .filter(payment -> "PAID".equals(payment.getStatus()))
                .filter(payment -> isSameDay(payment.getPaidDate(), today))
                .mapToDouble(FeeDetail::getAmount)
                .sum();
    }

    public int getTodayTransactionCount() {
        Date today = new Date();
        return (int) studentRepository.findAll().stream()
                .flatMap(student -> student.getFeePayments().stream())
                .filter(payment -> isSameDay(payment.getPaidDate(), today))
                .count();
    }

    public double calculateTotalPendingDues() {
        return studentRepository.findAll().stream()
                .mapToDouble(student -> calculateDueAmount(student.getId()))
                .sum();
    }

    public double calculateCollectionRate() {
        double totalFees = studentRepository.findAll().stream()
                .mapToDouble(student -> calculateTotalFees(student.getId()))
                .sum();
        return totalFees == 0 ? 0 : (calculateTotalRevenue() / totalFees) * 100;
    }

    public List<Map<String, Object>> getRevenueTrend() {
        // Returns last 6 months revenue trend
        List<Map<String, Object>> trend = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 5; i >= 0; i--) {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - i);
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", cal.getTime());
            monthData.put("revenue", calculateRevenueForMonth(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
            trend.add(monthData);
        }
        return trend;
    }

    private double calculateRevenueForMonth(int month, int year) {
        return studentRepository.findAll().stream()
                .flatMap(student -> student.getFeePayments().stream())
                .filter(payment -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(payment.getPaidDate());
                    return cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year;
                })
                .mapToDouble(FeeDetail::getAmount)
                .sum();
    }

    public Map<String, Double> getFeeTypeDistribution() {
        return studentRepository.findAll().stream()
                .flatMap(student -> student.getFeePayments().stream())
                .filter(payment -> "PAID".equals(payment.getStatus()))
                .collect(Collectors.groupingBy(
                        FeeDetail::getFeeType,
                        Collectors.summingDouble(FeeDetail::getAmount)
                ));
    }

    public List<Map<String, Object>> getRecentPayments() {
        return studentRepository.findAll().stream()
                .flatMap(student -> student.getFeePayments().stream()
                        .map(payment -> {
                            Map<String, Object> paymentData = new HashMap<>();
                            paymentData.put("studentName", student.getName());
                            paymentData.put("amount", payment.getAmount());
                            paymentData.put("date", payment.getPaidDate());
                            paymentData.put("type", payment.getFeeType());
                            return paymentData;
                        }))
                .sorted((p1, p2) -> ((Date) p2.get("date")).compareTo((Date) p1.get("date")))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDuePayments() {
        return studentRepository.findAll().stream()
                .filter(student -> calculateDueAmount(student.getId()) > 0)
                .map(student -> {
                    Map<String, Object> dueData = new HashMap<>();
                    dueData.put("studentName", student.getName());
                    dueData.put("studentId", student.getId());
                    dueData.put("dueAmount", calculateDueAmount(student.getId()));
                    dueData.put("lastPaymentDate", getLastPaymentDate(student));
                    return dueData;
                })
                .collect(Collectors.toList());
    }

    private Date getLastPaymentDate(Student student) {
        return student.getFeePayments().stream()
                .filter(payment -> "PAID".equals(payment.getStatus()))
                .map(FeeDetail::getPaidDate)
                .max(Date::compareTo)
                .orElse(null);
    }

    public void sendFeeReminder(Long studentId) {
        Student student = getStudentById(studentId);
        double dueAmount = calculateDueAmount(studentId);
        if (dueAmount > 0) {
            String subject = "Fee Payment Reminder";
            String message = String.format("Dear Parent, This is a reminder that %.2f is pending for %s's fees.",
                    dueAmount, student.getName());
            sendParentMessage(studentId, subject, message);
        }
    }

    public byte[] generateFeeReceipt(String transactionId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Fee Receipt");

            // Find the payment by transactionId
            Optional<FeeDetail> paymentOpt = studentRepository.findAll().stream()
                    .flatMap(student -> student.getFeePayments().stream())
                    .filter(payment -> transactionId.equals(payment.getTransactionId()))
                    .findFirst();

            if (paymentOpt.isEmpty()) {
                throw new RuntimeException("Payment not found for transaction: " + transactionId);
            }

            FeeDetail payment = paymentOpt.get();
            Student student = studentRepository.findAll().stream()
                    .filter(s -> s.getFeePayments().contains(payment))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Student not found for payment"));

            // Create receipt content
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Fee Receipt");

            Row studentRow = sheet.createRow(2);
            studentRow.createCell(0).setCellValue("Student Name:");
            studentRow.createCell(1).setCellValue(student.getName());

            Row dateRow = sheet.createRow(3);
            dateRow.createCell(0).setCellValue("Payment Date:");
            dateRow.createCell(1).setCellValue(payment.getPaidDate().toString());

            Row amountRow = sheet.createRow(4);
            amountRow.createCell(0).setCellValue("Amount Paid:");
            amountRow.createCell(1).setCellValue(payment.getAmount());

            Row typeRow = sheet.createRow(5);
            typeRow.createCell(0).setCellValue("Fee Type:");
            typeRow.createCell(1).setCellValue(payment.getFeeType());

            Row txnRow = sheet.createRow(6);
            txnRow.createCell(0).setCellValue("Transaction ID:");
            txnRow.createCell(1).setCellValue(payment.getTransactionId());

            // Auto-size columns
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating fee receipt", e);
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "ID", "Name", "Grade", "Email", "Phone", "Attendance %",
                "Parent Name", "Parent Email", "Registration Number"
        };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private void populateData(Sheet sheet) {
        int rowNum = 1;
        for (Student student : getAllStudents()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getId());
            row.createCell(1).setCellValue(student.getName());
            row.createCell(2).setCellValue(student.getGrade());
            row.createCell(3).setCellValue(student.getEmail());
            row.createCell(4).setCellValue(student.getPhoneNumber());
            row.createCell(5).setCellValue(student.getAttendancePercentage());
            row.createCell(6).setCellValue(student.getParentName());
            row.createCell(7).setCellValue(student.getParentEmail());
            row.createCell(8).setCellValue(student.getRegistrationNumber());
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private byte[] writeWorkbookToBytes(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    // Statistics methods
    public long getTotalStudents() {
        return studentRepository.count();
    }

    public double getAverageAttendance() {
        List<Student> students = getAllStudents();
        if (students.isEmpty()) {
            return 0.0;
        }
        return students.stream()
                .mapToDouble(Student::getAttendance)
                .average()
                .orElse(0.0);
    }

    public double getAverageGrade() {
        List<Student> students = getAllStudents();
        if (students.isEmpty()) {
            return 0.0;
        }
        return students.stream()
                .mapToDouble(student -> calculateAverageMarks(student.getId()))
                .average()
                .orElse(0.0);
    }

    public long getTotalCourses() {
        return studentRepository.findAll().stream()
                .flatMap(student -> student.getCourses().stream())
                .distinct()
                .count();
    }

    public Map<String, Long> getGradeDistribution() {
        return studentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Student::getGrade,
                        Collectors.counting()
                ));
    }

    public Map<String, Double> getAttendanceTrend() {
        Map<String, Double> trend = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");

        // Initialize with current month and go back 6 months
        for (int i = 5; i >= 0; i--) {
            cal.add(Calendar.MONTH, -1);
            String monthLabel = monthFormat.format(cal.getTime());
            List<Attendance> monthlyAttendance = attendanceRepository.findByDateBetween(
                    getMonthStart(cal.getTime()),
                    getMonthEnd(cal.getTime())
            );

            double attendancePercentage = 0.0;
            if (!monthlyAttendance.isEmpty()) {
                long presentCount = monthlyAttendance.stream()
                        .filter(Attendance::isPresent)
                        .count();
                attendancePercentage = (double) presentCount * 100 / monthlyAttendance.size();
            }
            trend.put(monthLabel, attendancePercentage);
        }
        return trend;
    }

    private Date getMonthStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private Date getMonthEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // Academic Progress Methods
    public Map<String, Object> getAcademicProgressData(Long studentId) {
        Student student = getStudentById(studentId);
        Map<String, Object> progress = new HashMap<>();
        progress.put("attendance", student.getAttendance());
        progress.put("averageMarks", calculateAverageMarks(studentId));
        progress.put("examEligibility", student.isEligibleForExam());
        progress.put("courseProgress", calculateCourseProgress(student));
        progress.put("recentPerformance", getRecentPerformance(student));
        return progress;
    }

    private Map<String, Double> calculateCourseProgress(Student student) {
        return student.getCourses().stream()
                .collect(Collectors.toMap(
                        Course::getName,
                        course -> calculateCourseCompletion(student, course)
                ));
    }

    private double calculateCourseCompletion(Student student, Course course) {
        // This is a placeholder implementation
        return student.getMarks().stream()
                .filter(mark -> mark.getSubject().equals(course.getName()))
                .count() * 100.0 / course.getTotalUnits();
    }

    private List<Map<String, Object>> getRecentPerformance(Student student) {
        return student.getMarks().stream()
                .sorted(Comparator.comparing(SubjectMark::getDate).reversed())
                .limit(5)
                .map(mark -> {
                    Map<String, Object> performance = new HashMap<>();
                    performance.put("subject", mark.getSubject());
                    performance.put("marks", mark.getMarks());
                    performance.put("date", mark.getDate());
                    return performance;
                })
                .collect(Collectors.toList());
    }

    // Attendance Report Methods
    public int getTotalClassesForStudent(Long studentId) {
        Student student = getStudentById(studentId);
        return student.getAttendanceRecords().size();
    }

    public int getAttendedClassesCount(Long studentId) {
        Student student = getStudentById(studentId);
        return (int) student.getAttendanceRecords().stream()
                .filter(record -> record.getStatus() == AttendanceStatus.PRESENT)
                .count();
    }

    public int getMissedClassesCount(Long studentId) {
        Student student = getStudentById(studentId);
        return (int) student.getAttendanceRecords().stream()
                .filter(record -> record.getStatus() == AttendanceStatus.ABSENT)
                .count();
    }

    public List<String> getTeacherComments(Long studentId) {
        Student student = getStudentById(studentId);
        return student.getAttendanceRecords().stream()
                .filter(record -> record.getNotes() != null && !record.getNotes().isEmpty())
                .map(AttendanceRecord::getNotes)
                .collect(Collectors.toList());
    }

    public List<String> getAreasForImprovement(Long studentId) {
        Student student = getStudentById(studentId);
        List<String> improvements = new ArrayList<>();

        // Check attendance
        if (student.getAttendance() < 75.0) {
            improvements.add("Attendance needs improvement");
        }

        // Check academic performance
        double avgMarks = calculateAverageMarks(studentId);
        if (avgMarks < 60.0) {
            improvements.add("Academic performance needs attention");
        }

        // Check fee payments
        if (student.hasUnpaidFees()) {
            improvements.add("Outstanding fees need to be cleared");
        }

        // Check subject-wise performance
        Map<String, Double> subjectAverages = student.getMarks().stream()
                .collect(Collectors.groupingBy(
                        SubjectMark::getSubject,
                        Collectors.averagingDouble(SubjectMark::getMarks)
                ));

        subjectAverages.forEach((subject, average) -> {
            if (average < 50.0) {
                improvements.add("Needs improvement in " + subject);
            }
        });

        return improvements;
    }
}
