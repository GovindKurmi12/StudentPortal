package com.gk.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.util.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Length(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Min(value = 5, message = "Age must be at least 5 years")
    @Max(value = 100, message = "Age must be less than 100 years")
    private int age;

    @Email(message = "Please provide a valid email address")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Grade is required")
    private String grade;

    @ElementCollection
    @CollectionTable(name = "student_marks", joinColumns = @JoinColumn(name = "student_id"))
    private List<SubjectMark> marks = new ArrayList<>();

    @DecimalMin(value = "0.0", message = "Attendance cannot be negative")
    @DecimalMax(value = "100.0", message = "Attendance cannot exceed 100%")
    private double attendance;

    @Length(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Past(message = "Date of birth must be in the past")
    @Temporal(TemporalType.DATE)
    private java.util.Date dateOfBirth;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttendanceRecord> attendanceRecords = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "student_fees", joinColumns = @JoinColumn(name = "student_id"))
    private List<FeeDetail> feePayments = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudentEvent> events = new ArrayList<>();

    @Email(message = "Please provide a valid parent email address")
    private String parentEmail;

    @Pattern(regexp = "\\d{10}", message = "Parent phone number must be 10 digits")
    private String parentPhone;

    private String bloodGroup;

    @NotBlank(message = "Emergency contact is required")
    private String emergencyContact;

    @Column(name = "admission_date")
    @Temporal(TemporalType.DATE)
    private Date admissionDate;

    @Column(name = "registration_number", unique = true)
    private String registrationNumber;

    private String section;

    @Column(name = "roll_number")
    private String rollNumber;

    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_occupation")
    private String parentOccupation;

    @Column(name = "annual_income")
    private Double annualIncome;

    @Column(name = "attendance_percentage")
    private Double attendancePercentage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Embeddable
    public static class FeeDetail {
        @NotNull
        private String feeType;

        @NotNull
        @Positive
        private Double amount;

        @NotNull
        @Temporal(TemporalType.TIMESTAMP)
        private Date paidDate;

        @NotNull
        private String status;

        @NotNull
        private String transactionId;

        private String remarks;

        public FeeDetail() {}

        public FeeDetail(String feeType, Double amount, Date paidDate, String status, String transactionId, String remarks) {
            this.feeType = feeType;
            this.amount = amount;
            this.paidDate = paidDate;
            this.status = status;
            this.transactionId = transactionId;
            this.remarks = remarks;
        }

        public String getFeeType() {
            return feeType;
        }

        public void setFeeType(String feeType) {
            this.feeType = feeType;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public Date getPaidDate() {
            return paidDate;
        }

        public void setPaidDate(Date paidDate) {
            this.paidDate = paidDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FeeDetail feeDetail = (FeeDetail) o;
            return Objects.equals(feeType, feeDetail.feeType) &&
                   Objects.equals(amount, feeDetail.amount) &&
                   Objects.equals(paidDate, feeDetail.paidDate) &&
                   Objects.equals(status, feeDetail.status) &&
                   Objects.equals(transactionId, feeDetail.transactionId) &&
                   Objects.equals(remarks, feeDetail.remarks);
        }

        @Override
        public int hashCode() {
            return Objects.hash(feeType, amount, paidDate, status, transactionId, remarks);
        }

        @Override
        public String toString() {
            return "FeeDetail{" +
                   "feeType='" + feeType + '\'' +
                   ", amount=" + amount +
                   ", paidDate=" + paidDate +
                   ", status='" + status + '\'' +
                   ", transactionId='" + transactionId + '\'' +
                   ", remarks='" + remarks + '\'' +
                   '}';
        }
    }

    @PrePersist
    protected void onCreate() {
        if (admissionDate == null) {
            admissionDate = new Date();
        }
        lastModifiedDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = new Date();
    }

    // Helper methods
    public boolean hasUnpaidFees() {
        return feePayments.stream()
                .filter(fee -> "UNPAID".equals(fee.getStatus()))
                .mapToDouble(FeeDetail::getAmount)
                .sum() > 0;
    }

    public boolean isAttendanceBelow(double threshold) {
        return attendance < threshold;
    }

    public boolean isEligibleForExam() {
        return attendance >= 75.0 && !hasUnpaidFees();
    }

    public String getFullName() {
        return name;
    }

    public int getAge() {
        if (dateOfBirth == null) {
            return age;
        }
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubjectMark> getMarks() {
        return marks;
    }

    public void setMarks(List<SubjectMark> marks) {
        this.marks = marks;
    }

    public List<AttendanceRecord> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void setAttendanceRecords(List<AttendanceRecord> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    public List<FeeDetail> getFeePayments() {
        return feePayments;
    }

    public void setFeePayments(List<FeeDetail> feePayments) {
        this.feePayments = feePayments;
    }

    public List<StudentEvent> getEvents() {
        return events;
    }

    public void setEvents(List<StudentEvent> events) {
        this.events = events;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getAttendance() {
        return attendance;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentOccupation() {
        return parentOccupation;
    }

    public void setParentOccupation(String parentOccupation) {
        this.parentOccupation = parentOccupation;
    }

    public Double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public Double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(Double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) &&
                Objects.equals(email, student.email) &&
                Objects.equals(registrationNumber, student.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, registrationNumber);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", grade='" + grade + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                '}';
    }
}
