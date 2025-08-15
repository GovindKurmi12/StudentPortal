package com.gk.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "attendance_records",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "date"}))
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Date is required")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(length = 1000)
    private String notes;

    @Column(name = "marked_by", nullable = false)
    private String markedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "marked_at")
    private Date markedAt;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "session_type")
    private String sessionType;

    public AttendanceRecord() {}

    public AttendanceRecord(Long id, Student student, Date date, AttendanceStatus status,
                          String notes, String markedBy, Date markedAt,
                          Integer lateMinutes, String sessionType) {
        this.id = id;
        this.student = student;
        this.date = date;
        this.status = status;
        this.notes = notes;
        this.markedBy = markedBy;
        this.markedAt = markedAt;
        this.lateMinutes = lateMinutes;
        this.sessionType = sessionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(String markedBy) {
        this.markedBy = markedBy;
    }

    public Date getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(Date markedAt) {
        this.markedAt = markedAt;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public enum AttendanceStatus {
        PRESENT("Present"),
        ABSENT("Absent"),
        LATE("Late"),
        EXCUSED("Excused");

        private final String displayName;

        AttendanceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        markedAt = new Date();
    }

    public boolean isPresent() {
        return status == AttendanceStatus.PRESENT;
    }

    public boolean isAbsent() {
        return status == AttendanceStatus.ABSENT;
    }

    public boolean isLate() {
        return status == AttendanceStatus.LATE;
    }

    public boolean isExcused() {
        return status == AttendanceStatus.EXCUSED;
    }

    public boolean isValidStatus() {
        return status != null;
    }

    public void markLate(int minutes) {
        this.status = AttendanceStatus.LATE;
        this.lateMinutes = minutes;
    }

    public void markExcused(String reason) {
        this.status = AttendanceStatus.EXCUSED;
        this.notes = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceRecord that = (AttendanceRecord) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(student, that.student) &&
               Objects.equals(date, that.date) &&
               status == that.status &&
               Objects.equals(notes, that.notes) &&
               Objects.equals(markedBy, that.markedBy) &&
               Objects.equals(markedAt, that.markedAt) &&
               Objects.equals(lateMinutes, that.lateMinutes) &&
               Objects.equals(sessionType, that.sessionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student, date, status, notes, markedBy,
                          markedAt, lateMinutes, sessionType);
    }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
               "id=" + id +
               ", student=" + (student != null ? student.getId() : null) +
               ", date=" + date +
               ", status=" + status +
               ", notes='" + notes + '\'' +
               ", markedBy='" + markedBy + '\'' +
               ", markedAt=" + markedAt +
               ", lateMinutes=" + lateMinutes +
               ", sessionType='" + sessionType + '\'' +
               '}';
    }

    public static AttendanceRecordBuilder builder() {
        return new AttendanceRecordBuilder();
    }

    public static class AttendanceRecordBuilder {
        private Long id;
        private Student student;
        private Date date;
        private AttendanceStatus status;
        private String notes;
        private String markedBy;
        private Date markedAt;
        private Integer lateMinutes;
        private String sessionType;

        AttendanceRecordBuilder() {}

        public AttendanceRecordBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AttendanceRecordBuilder student(Student student) {
            this.student = student;
            return this;
        }

        public AttendanceRecordBuilder date(Date date) {
            this.date = date;
            return this;
        }

        public AttendanceRecordBuilder status(AttendanceStatus status) {
            this.status = status;
            return this;
        }

        public AttendanceRecordBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public AttendanceRecordBuilder markedBy(String markedBy) {
            this.markedBy = markedBy;
            return this;
        }

        public AttendanceRecordBuilder markedAt(Date markedAt) {
            this.markedAt = markedAt;
            return this;
        }

        public AttendanceRecordBuilder lateMinutes(Integer lateMinutes) {
            this.lateMinutes = lateMinutes;
            return this;
        }

        public AttendanceRecordBuilder sessionType(String sessionType) {
            this.sessionType = sessionType;
            return this;
        }

        public AttendanceRecord build() {
            return new AttendanceRecord(id, student, date, status, notes,
                                      markedBy, markedAt, lateMinutes, sessionType);
        }
    }
}
