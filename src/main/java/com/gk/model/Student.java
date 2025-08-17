package com.gk.model;

import com.gk.dto.FeeDetail;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "students")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    private LocalDate dateOfBirth;

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

    private String emergencyContact;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

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

    @Transient
    private double averageScore;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        if (admissionDate == null) {
            admissionDate = LocalDate.now();
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

    public boolean isEligibleForExam() {
        return attendance >= 75.0 && !hasUnpaidFees();
    }

    public int getAge() {
        if (dateOfBirth == null) {
            return age;
        }
        return (int) java.time.temporal.ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }
}
