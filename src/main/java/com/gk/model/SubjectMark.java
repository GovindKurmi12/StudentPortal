package com.gk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectMark {
    @NotBlank(message = "Subject name is required")
    @Length(min = 2, max = 50, message = "Subject name must be between 2 and 50 characters")
    @Column(nullable = false)
    private String subject;

    @DecimalMin(value = "0.0", message = "Marks cannot be negative")
    @DecimalMax(value = "100.0", message = "Marks cannot exceed 100")
    private double marks;

    @Pattern(regexp = "^[A-F][+-]?$", message = "Grade must be A+, A, A-, B+, B, B-, C+, C, C-, D+, D, D-, F")
    private String grade;

    @Temporal(TemporalType.DATE)
    private Date date;

    @NotBlank(message = "Term is required")
    private String term;

    @Length(max = 255, message = "Remarks must not exceed 255 characters")
    private String remarks;

    @Length(max = 1000, message = "Comments must not exceed 1000 characters")
    @Column(length = 1000)
    private String comments;

    // Additional fields for extended functionality
    private String examType;
    private boolean isPublished;
    private Date submissionDate;

    @Length(max = 255, message = "Teacher name must not exceed 255 characters")
    private String teacherName;

    public String calculateGrade() {
        if (marks >= 97) return "A+";
        if (marks >= 93) return "A";
        if (marks >= 90) return "A-";
        if (marks >= 87) return "B+";
        if (marks >= 83) return "B";
        if (marks >= 80) return "B-";
        if (marks >= 77) return "C+";
        if (marks >= 73) return "C";
        if (marks >= 70) return "C-";
        if (marks >= 67) return "D+";
        if (marks >= 63) return "D";
        if (marks >= 60) return "D-";
        return "F";
    }

    public void setMarks(double marks) {
        this.marks = marks;
        this.grade = calculateGrade();
    }

    // Helper methods
    public boolean isPassing() {
        return marks >= 60.0;
    }

    public String getStatus() {
        return isPassing() ? "PASS" : "FAIL";
    }

}
