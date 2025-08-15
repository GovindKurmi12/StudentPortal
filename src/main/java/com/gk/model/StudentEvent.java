package com.gk.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class StudentEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Length(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotNull(message = "Start date/time is required")
    @Future(message = "Event must be scheduled in the future")
    private LocalDateTime start;

    @Column(length = 1000)
    @Length(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Event type is required")
    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column(nullable = false)
    private String status = "SCHEDULED";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "location")
    private String location;

    @Column(name = "duration_minutes")
    @Min(value = 0, message = "Duration cannot be negative")
    private Integer durationMinutes;

    @Column(name = "reminder_sent")
    private boolean reminderSent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    public StudentEvent() {}

    public StudentEvent(Long id, String title, LocalDateTime start, String description, EventType type,
                       String status, Student student, String location, Integer durationMinutes,
                       boolean reminderSent, LocalDateTime createdAt, LocalDateTime modifiedAt,
                       String createdBy, String modifiedBy) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.description = description;
        this.type = type;
        this.status = status;
        this.student = student;
        this.location = location;
        this.durationMinutes = durationMinutes;
        this.reminderSent = reminderSent;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }

    // Helper methods
    public LocalDateTime getEndTime() {
        return durationMinutes != null ? start.plusMinutes(durationMinutes) : start.plusHours(1);
    }

    public boolean isUpcoming() {
        return start.isAfter(LocalDateTime.now());
    }

    public boolean isPast() {
        return start.isBefore(LocalDateTime.now());
    }

    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return start.isBefore(now) && getEndTime().isAfter(now);
    }

    public boolean needsReminder() {
        return !reminderSent && isUpcoming() &&
               start.minusHours(24).isBefore(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentEvent that = (StudentEvent) o;
        return reminderSent == that.reminderSent &&
               Objects.equals(id, that.id) &&
               Objects.equals(title, that.title) &&
               Objects.equals(start, that.start) &&
               Objects.equals(description, that.description) &&
               type == that.type &&
               Objects.equals(status, that.status) &&
               Objects.equals(student, that.student) &&
               Objects.equals(location, that.location) &&
               Objects.equals(durationMinutes, that.durationMinutes) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(modifiedAt, that.modifiedAt) &&
               Objects.equals(createdBy, that.createdBy) &&
               Objects.equals(modifiedBy, that.modifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, start, description, type, status, student,
                          location, durationMinutes, reminderSent, createdAt,
                          modifiedAt, createdBy, modifiedBy);
    }

    @Override
    public String toString() {
        return "StudentEvent{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", start=" + start +
               ", description='" + description + '\'' +
               ", type=" + type +
               ", status='" + status + '\'' +
               ", student=" + (student != null ? student.getId() : null) +
               ", location='" + location + '\'' +
               ", durationMinutes=" + durationMinutes +
               ", reminderSent=" + reminderSent +
               ", createdAt=" + createdAt +
               ", modifiedAt=" + modifiedAt +
               ", createdBy='" + createdBy + '\'' +
               ", modifiedBy='" + modifiedBy + '\'' +
               '}';
    }

    public static StudentEventBuilder builder() {
        return new StudentEventBuilder();
    }

    public static class StudentEventBuilder {
        private Long id;
        private String title;
        private LocalDateTime start;
        private String description;
        private EventType type;
        private String status = "SCHEDULED";
        private Student student;
        private String location;
        private Integer durationMinutes;
        private boolean reminderSent;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String createdBy;
        private String modifiedBy;

        StudentEventBuilder() {}

        public StudentEventBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudentEventBuilder title(String title) {
            this.title = title;
            return this;
        }

        public StudentEventBuilder start(LocalDateTime start) {
            this.start = start;
            return this;
        }

        public StudentEventBuilder description(String description) {
            this.description = description;
            return this;
        }

        public StudentEventBuilder type(EventType type) {
            this.type = type;
            return this;
        }

        public StudentEventBuilder status(String status) {
            this.status = status;
            return this;
        }

        public StudentEventBuilder student(Student student) {
            this.student = student;
            return this;
        }

        public StudentEventBuilder location(String location) {
            this.location = location;
            return this;
        }

        public StudentEventBuilder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public StudentEventBuilder reminderSent(boolean reminderSent) {
            this.reminderSent = reminderSent;
            return this;
        }

        public StudentEventBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StudentEventBuilder modifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public StudentEventBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public StudentEventBuilder modifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public StudentEvent build() {
            return new StudentEvent(id, title, start, description, type, status,
                                  student, location, durationMinutes, reminderSent,
                                  createdAt, modifiedAt, createdBy, modifiedBy);
        }
    }
}
