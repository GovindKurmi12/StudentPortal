package com.gk.model;

import com.gk.dto.AttendanceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
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


    @PrePersist
    protected void onCreate() {
        markedAt = new Date();
    }
}
