package com.gk.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Temporal(TemporalType.DATE)
    private Date date;

    private boolean present;

    @Column(length = 500)
    private String notes;

    @Column(length = 50)
    private String markedBy;
}
