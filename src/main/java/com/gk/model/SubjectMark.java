package com.gk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.Objects;

@Embeddable
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

    public SubjectMark() {}

    public SubjectMark(String subject, double marks, String grade, Date date, String term, String remarks,
                      String comments, String examType, boolean isPublished, Date submissionDate, String teacherName) {
        this.subject = subject;
        this.marks = marks;
        this.grade = grade;
        this.date = date;
        this.term = term;
        this.remarks = remarks;
        this.comments = comments;
        this.examType = examType;
        this.isPublished = isPublished;
        this.submissionDate = submissionDate;
        this.teacherName = teacherName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getMarks() {
        return marks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

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

    public boolean isLate() {
        return submissionDate != null && date != null && submissionDate.after(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectMark that = (SubjectMark) o;
        return Double.compare(that.marks, marks) == 0 &&
               isPublished == that.isPublished &&
               Objects.equals(subject, that.subject) &&
               Objects.equals(grade, that.grade) &&
               Objects.equals(date, that.date) &&
               Objects.equals(term, that.term) &&
               Objects.equals(remarks, that.remarks) &&
               Objects.equals(comments, that.comments) &&
               Objects.equals(examType, that.examType) &&
               Objects.equals(submissionDate, that.submissionDate) &&
               Objects.equals(teacherName, that.teacherName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, marks, grade, date, term, remarks, comments,
                          examType, isPublished, submissionDate, teacherName);
    }

    @Override
    public String toString() {
        return "SubjectMark{" +
               "subject='" + subject + '\'' +
               ", marks=" + marks +
               ", grade='" + grade + '\'' +
               ", date=" + date +
               ", term='" + term + '\'' +
               ", remarks='" + remarks + '\'' +
               ", comments='" + comments + '\'' +
               ", examType='" + examType + '\'' +
               ", isPublished=" + isPublished +
               ", submissionDate=" + submissionDate +
               ", teacherName='" + teacherName + '\'' +
               '}';
    }
}
